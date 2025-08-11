package com.codezj.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import com.codezj.domain.trade.adapter.repository.ITradeRepository;
import com.codezj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.codezj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.codezj.domain.trade.model.entity.*;
import com.codezj.domain.trade.model.valobj.NotifyStatusEnum;
import com.codezj.domain.trade.model.valobj.TradeOrderProgressVO;
import com.codezj.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import com.codezj.infrastructure.dao.IGroupBuyActivityDao;
import com.codezj.infrastructure.dao.IGroupBuyOrderDao;
import com.codezj.infrastructure.dao.IGroupBuyOrderListDao;
import com.codezj.infrastructure.dao.INotifyTaskDao;
import com.codezj.infrastructure.dao.po.GroupBuyActivity;
import com.codezj.infrastructure.dao.po.GroupBuyOrder;
import com.codezj.infrastructure.dao.po.GroupBuyOrderList;
import com.codezj.infrastructure.dao.po.NotifyTask;
import com.codezj.infrastructure.rcc.RCCService;
import com.codezj.types.enums.ActivityStatusEnum;
import com.codezj.types.enums.GroupBuyStatusEnum;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codezj.types.common.Constants.UNDER_LINE;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Slf4j
@Repository
public class TradeRepository implements ITradeRepository {

    @Resource
    private IGroupBuyActivityDao groupBuyActivityDao;

    @Resource
    private IGroupBuyOrderDao groupBuyOrderDao;

    @Resource
    private IGroupBuyOrderListDao groupBuyOrderListDao;

    @Resource
    private INotifyTaskDao notifyTaskDao;

    @Resource
    private RCCService rccService;

    @Override
    public MarketPayOrderEntity queryNotPayOrderByOutTradeNo(String userId, String outTradeNo) {
        GroupBuyOrderList groupBuyOrderListReq = GroupBuyOrderList.builder()
                .userId(userId)
                .outTradeNo(outTradeNo)
                .status(TradeOrderStatusEnumVO.CREATE.getCode())
                .build();
        GroupBuyOrderList groupBuyOrderRecord = groupBuyOrderListDao.queryGroupBuyOrderRecordByOutTradeNo(groupBuyOrderListReq);
        if (Objects.isNull(groupBuyOrderRecord)) {
            log.warn("[仓储服务] 未查询到订单。userId: {}, outTradeNo: {}", userId, outTradeNo);
            return null;
        }
        return MarketPayOrderEntity.builder()
                .teamId(groupBuyOrderRecord.getTeamId())
                .orderId(groupBuyOrderRecord.getOrderId())
                .deductionPrice(groupBuyOrderRecord.getDeductionPrice())
                .tradeOrderStatusEnumVO(TradeOrderStatusEnumVO.getByCode(groupBuyOrderRecord.getStatus()))
                .build();
    }

    @Override
    public TradeOrderProgressVO queryTradeOrderProgress(String teamId) {
        GroupBuyOrder groupBuyOrder = groupBuyOrderDao.queryGroupBuyProgressByTeamId(teamId);
        if (Objects.isNull(groupBuyOrder)) {
            log.warn("[仓储服务] 未查询到拼团订单。teamId: {}", teamId);
            return null;
        }
        return TradeOrderProgressVO.builder()
                .targetCount(groupBuyOrder.getTargetCount())
                .completeCount(groupBuyOrder.getCompleteCount())
                .lockCount(groupBuyOrder.getLockCount())
                .build();
    }

    @Transactional(timeout = 500)
    @Override
    public MarketPayOrderEntity lockGroupBuyTradeOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) throws Exception {

        UserEntity userEntity = groupBuyOrderAggregate.getUserEntity();
        PayActivityEntity payActivityEntity = groupBuyOrderAggregate.getPayActivityEntity();
        PayDiscountEntity payDiscountEntity = groupBuyOrderAggregate.getPayDiscountEntity();
        Integer userTakeCount = groupBuyOrderAggregate.getUserTakeCount();

        // 拼团是否存在
        String teamId = payActivityEntity.getTeamId();
        try {
            if (StringUtils.isBlank(teamId)) {
                log.info("[仓储服务] 拼团不存在，创建新团");
                teamId = RandomStringUtils.randomNumeric(8);
                // 创建拼团
                GroupBuyOrder groupBuyOrder = GroupBuyOrder.builder()
                        .teamId(teamId)
                        .activityId(payActivityEntity.getActivityId())
                        .source(payDiscountEntity.getSource())
                        .channel(payDiscountEntity.getChannel())
                        .originalPrice(payDiscountEntity.getOriginalPrice())
                        .deductionPrice(payDiscountEntity.getDeductionPrice())
                        .payPrice(payDiscountEntity.getPayPrice())
                        .targetCount(payActivityEntity.getTargetCount())
                        .completeCount(0)
                        .lockCount(1)
                        .status(0)
                        .notifyUrl(payActivityEntity.getNotifyUrl())
                        .validStartTime(LocalDateTime.now())
                        .validEndTime(LocalDateTime.now().plusMinutes(Integer.parseInt(payActivityEntity.getValidTime())))
                        .build();
                try {
                    groupBuyOrderDao.insert(groupBuyOrder);
                } catch (Exception e) {
                    throw new RuntimeException("创建拼团失败", e);
                }
            } else {
                // 拼团人数是否合法，锁单量 = 已经支付完成 + 创建订单尚未支付 <= 目标量
                GroupBuyTeamEntity groupBuyTeamEntity = queryGroupBuyTeam(teamId);
                if (Objects.isNull(groupBuyTeamEntity)) {
                    log.error("[仓储服务] 拼团不存在，teamId: {}", teamId);
                    throw new RuntimeException("拼团不存在");
                }
                if (groupBuyTeamEntity.getTargetCount() <= groupBuyTeamEntity.getLockCount()) {
                    log.error("[仓储服务] 拼团人数已满，teamId: {}", teamId);
                    throw new RuntimeException("当前拼团人已满");
                }
                if (GroupBuyStatusEnum.FAIL.equals(groupBuyTeamEntity.getStatus()) || GroupBuyStatusEnum.COMPLETE.equals(groupBuyTeamEntity.getStatus())) {
                    log.error("[仓储服务] 拼团已失败，teamId: {}", teamId);
                    throw new RuntimeException("当前拼团已结束");
                }
                if (LocalDateTime.now().isAfter(groupBuyTeamEntity.getValidEndTime())) {
                    log.error("[仓储服务] 拼团已过期，teamId: {}", teamId);
                    throw new RuntimeException("当前拼团已结束");
                }
                // 增加锁单量
                groupBuyOrderDao.updateAddLockCount(teamId);
            }
            // 订单号
            String orderId = RandomStringUtils.randomNumeric(12);
            // 业务id
            String bizId = payActivityEntity.getActivityId() + UNDER_LINE + userEntity.getUserId() + UNDER_LINE + (userTakeCount + 1);
            // 创建订单
            GroupBuyOrderList groupBuyOrderList = GroupBuyOrderList.builder()
                    .userId(userEntity.getUserId())
                    .teamId(teamId)
                    .orderId(orderId)
                    .activityId(payActivityEntity.getActivityId())
                    .startTime(payActivityEntity.getStartTime())
                    .endTime(payActivityEntity.getEndTime())
                    .goodsId(payDiscountEntity.getGoodsId())
                    .source(payDiscountEntity.getSource())
                    .channel(payDiscountEntity.getChannel())
                    .originalPrice(payDiscountEntity.getOriginalPrice())
                    .deductionPrice(payDiscountEntity.getDeductionPrice())
                    .status(TradeOrderStatusEnumVO.CREATE.getCode())
                    .outTradeNo(payActivityEntity.getOutTradeNo())
                    .bizId(bizId)
                    .build();
            // 插入新订单
            int insert = 0;
            try {
                insert = groupBuyOrderListDao.insert(groupBuyOrderList);
            } catch (Exception e) {
                throw new RuntimeException("插入拼团订单失败");
            }
            if (insert <= 0) {
                log.error("[仓储服务] 拼团数据增加失败");
                return null;
            }
            log.info("[仓储服务] 拼团数据增加成功");
            return MarketPayOrderEntity.builder()
                    .orderId(orderId)
                    .deductionPrice(payDiscountEntity.getDeductionPrice())
                    .tradeOrderStatusEnumVO(TradeOrderStatusEnumVO.CREATE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("未知错误，锁单失败");
        }

    }

    @Override
    public GroupBuyActivityEntity getGroupBuyActivity(Long activityId) {
        GroupBuyActivity groupBuyActivity = groupBuyActivityDao.queryGroupBuyActivityByActivityId(activityId);
        if (Objects.isNull(groupBuyActivity)) {
            log.warn("[仓储服务] 未查询到活动。activityId: {}", activityId);
            return null;
        }
        return GroupBuyActivityEntity.builder()
                .activityId(groupBuyActivity.getActivityId())
                .activityName(groupBuyActivity.getActivityName())
                .discountId(groupBuyActivity.getDiscountId())
                .groupType(groupBuyActivity.getGroupType())
                .tagId(groupBuyActivity.getTagId())
                .takeLimitCount(groupBuyActivity.getTakeLimitCount())
                .target(groupBuyActivity.getTarget())
                .validTime(groupBuyActivity.getValidTime())
                .status(ActivityStatusEnum.getByCode(groupBuyActivity.getStatus()))
                .startTime(groupBuyActivity.getStartTime())
                .endTime(groupBuyActivity.getEndTime())
                .tagScope(groupBuyActivity.getTagScope())
                .build();
    }

    @Override
    public Integer queryUserTakeCountByActivityId(String userId, Long activityId) {
        return groupBuyOrderListDao.queryUserTakeCountByActivityId(GroupBuyOrderList.builder()
                .userId(userId).activityId(activityId).build());
    }

    @Override
    public Integer updatePayOrderStatus2COMPLETE(String userId, String outTradeNo, Integer status) {
        GroupBuyOrderList groupBuyOrderListReq = GroupBuyOrderList.builder()
                .userId(userId)
                .outTradeNo(outTradeNo)
                .status(status)
                .build();
        return groupBuyOrderListDao.updateOrderStatus2COMPLETE(groupBuyOrderListReq);
    }

    @Override
    public GroupBuyTeamEntity queryGroupBuyTeam(String teamId) {
        GroupBuyOrder groupBuyOrder = groupBuyOrderDao.queryGroupBuyByTeamId(teamId);
        if (Objects.isNull(groupBuyOrder)) {
            log.warn("[仓储服务] 未查询到拼团。teamId: {}", teamId);
            return null;
        }
        return GroupBuyTeamEntity.builder()
                .teamId(groupBuyOrder.getTeamId())
                .activityId(groupBuyOrder.getActivityId())
                .targetCount(groupBuyOrder.getTargetCount())
                .completeCount(groupBuyOrder.getCompleteCount())
                .lockCount(groupBuyOrder.getLockCount())
                .status(GroupBuyStatusEnum.valueOf(groupBuyOrder.getStatus()))
                .validStartTime(groupBuyOrder.getValidStartTime())
                .validEndTime(groupBuyOrder.getValidEndTime())
                .notifyUrl(groupBuyOrder.getNotifyUrl())
                .build();
    }

    @Transactional(timeout = 500)
    @Override
    public void settleGroupBuyOrder(GroupBuyTeamSettlementAggregate groupBuyTeamSettlementAggregate) {
        // 处理三张表：用户订单表、拼团表、回调任务表
        // 同一个事务下
        UserEntity userEntity = groupBuyTeamSettlementAggregate.getUserEntity();
        GroupBuyTeamEntity groupBuyTeamEntity = groupBuyTeamSettlementAggregate.getGroupBuyTeamEntity();
        SettlementReqEntity settlementReqEntity = groupBuyTeamSettlementAggregate.getSettlementReqEntity();

        // 更新用户订单表
        int updatedOrderStatusCount = groupBuyOrderListDao.updateOrderStatus2COMPLETE(GroupBuyOrderList.builder()
                .userId(userEntity.getUserId())
                .outTradeNo(settlementReqEntity.getOutTradeNo())
                .outTradeTime(settlementReqEntity.getOutTradeTime())
                .build());
        if (1 != updatedOrderStatusCount) {
            log.error("[仓储服务] 用户订单状态更新失败");
            throw new AppException(ResponseCode.REPOSITORY_UPDATE_ZERO.getCode(), ResponseCode.REPOSITORY_UPDATE_ZERO.getInfo());
        }

        // 更新拼团表
        int updateAddCompleteCount = groupBuyOrderDao.updateAddCompleteCount(groupBuyTeamEntity.getTeamId());
        if (1 != updateAddCompleteCount) {
            log.error("[仓储服务] 拼团完成数量更新失败");
            throw new AppException(ResponseCode.REPOSITORY_UPDATE_ZERO.getCode(), ResponseCode.REPOSITORY_UPDATE_ZERO.getInfo());
        }

        // 如果拼团完成，更新拼团表
        if (groupBuyTeamEntity.getTargetCount() - groupBuyTeamEntity.getCompleteCount() == 1) {
            int updateGroupBuyOrderStatus2COMPLETECount = groupBuyOrderDao.updateGroupBuyOrderStatus2COMPLETE(groupBuyTeamEntity.getTeamId());
            if (1 != updateGroupBuyOrderStatus2COMPLETECount) {
                log.error("[仓储服务] 拼团状态更新失败");
                throw new AppException(ResponseCode.REPOSITORY_UPDATE_ZERO.getCode(), ResponseCode.REPOSITORY_UPDATE_ZERO.getInfo());
            }

            // 获取拼团中的成员的外部交易号
            List<String> outOrderNos = groupBuyOrderListDao.queryGroupBuyCompleteOutOrderNosByTeamId(groupBuyTeamEntity.getTeamId());

            NotifyTask notifyTask = NotifyTask.builder()
                    .activityId(groupBuyTeamEntity.getActivityId())
                    .teamId(groupBuyTeamEntity.getTeamId())
                    .notifyStatus(NotifyStatusEnum.CREATE.getCode())
                    .notifyCount(0)
                    .notifyUrl(groupBuyTeamEntity.getNotifyUrl())
                    .parameterJson(JSON.toJSONString(new HashMap<String, Object>() {{
                        put("teamId", groupBuyTeamEntity.getTeamId());
                        put("outTradeNo", outOrderNos);
                    }}))
                    .build();
            notifyTaskDao.insert(notifyTask);

            log.info("[仓储服务] 拼团完成，插入回调任务表，等待通知用户");

        }
    }

    @Override
    public boolean isInSCBlockList(String source, String channel) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(channel)) {
            return false;
        }
        return rccService.isInSCBlockList(source, channel);
    }

    @Override
    public List<NotifyTaskEntity> queryUnExecutedNotifyTaskList() {
        List<NotifyTask> notifyTasks = notifyTaskDao.queryUnExecutedNotifyTaskList();
        return notifyTasks.stream().map(this::convert2NotifyTaskEntity).collect(Collectors.toList());
    }



    @Override
    public List<NotifyTaskEntity> queryUnExecutedNotifyTaskList(String teamId) {
        List<NotifyTask> notifyTasks = notifyTaskDao.queryUnExecutedNotifyTaskListByTeamId(teamId);
        return notifyTasks.stream().map(this::convert2NotifyTaskEntity).collect(Collectors.toList());
    }

    @Override
    public int updateNotifyTaskStatusSuccess(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusSuccess(teamId);
    }

    @Override
    public int updateNotifyTaskStatusError(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusError(teamId);
    }

    @Override
    public int updateNotifyTaskStatusRetry(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusRetry(teamId);
    }

    private NotifyTaskEntity convert2NotifyTaskEntity(NotifyTask notifyTask) {
        return NotifyTaskEntity.builder()
                .activityId(notifyTask.getActivityId())
                .teamId(notifyTask.getTeamId())
                .notifyUrl(notifyTask.getNotifyUrl())
                .notifyCount(notifyTask.getNotifyCount())
                .notifyStatus(notifyTask.getNotifyStatus())
                .parameterJson(notifyTask.getParameterJson())
                .build();
    }
}
