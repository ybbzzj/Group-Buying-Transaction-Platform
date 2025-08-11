package com.codezj.infrastructure.adapter.repository;

import com.codezj.domain.activity.adapter.repository.IActivityRepository;
import com.codezj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SCSkuActivityVO;
import com.codezj.domain.activity.model.valobj.SkuVO;
import com.codezj.domain.activity.model.valobj.TeamStatisticVO;
import com.codezj.infrastructure.dao.*;
import com.codezj.infrastructure.dao.po.*;
import com.codezj.infrastructure.rcc.RCCService;
import com.codezj.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBitSet;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动仓储实现
 */
@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IGroupBuyActivityDao groupBuyActivityDao;

    @Resource
    private IGroupBuyDiscountDao groupBuyDiscountDao;

    @Resource
    private ISkuDao skuDao;

    @Resource
    private ISCSkuActivityDao scSkuActivityDao;


    @Resource
    private IRedisService redisService;

    @Resource
    private RCCService rccService;

    @Resource
    private IGroupBuyOrderDao groupBuyOrderDao;

    @Resource
    private IGroupBuyOrderListDao groupBuyOrderListDao;


    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(Long activityId) {
        // 根据拼团活动Id查询拼团活动配置
        GroupBuyActivity groupBuyActivityReq = GroupBuyActivity.builder().activityId(activityId).build();
        GroupBuyActivity groupBuyActivityResp = groupBuyActivityDao.queryValidGroupBuyActivity(groupBuyActivityReq);
        if (Objects.isNull(groupBuyActivityResp)) {
            log.error("[活动仓储服务]拼团活动表未查询到有效的活动配置, activityId:{}", activityId);
            return null;
        }
        // 根据活动配置查询折扣配置
        Long DiscountId = groupBuyActivityResp.getDiscountId();
        GroupBuyDiscount groupBuyDiscountResp = groupBuyDiscountDao.queryGroupBuyActivityDiscountByDiscountId(DiscountId);
        if (Objects.isNull(groupBuyDiscountResp)) {
            log.error("[活动仓储服务]拼团活动折扣表未查询到有效的活动折扣配置, discountId:{}", DiscountId);
            return null;
        }

        // 构建折扣配置信息
        GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount = GroupBuyActivityDiscountVO.GroupBuyDiscount.builder()
                .discountName(groupBuyDiscountResp.getDiscountName())
                .discountDesc(groupBuyDiscountResp.getDiscountDesc())
                .discountType(groupBuyDiscountResp.getDiscountType())
                .marketPlan(groupBuyDiscountResp.getMarketPlan())
                .marketExpr(groupBuyDiscountResp.getMarketExpr())
                .tagId(groupBuyDiscountResp.getTagId())
                .build();

        // 构建活动配置信息
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = GroupBuyActivityDiscountVO.builder()
                .activityId(groupBuyActivityResp.getActivityId())
                .activityName(groupBuyActivityResp.getActivityName())
                .groupBuyDiscount(groupBuyDiscount)
                .groupType(groupBuyActivityResp.getGroupType())
                .takeLimitCount(groupBuyActivityResp.getTakeLimitCount())
                .target(groupBuyActivityResp.getTarget())
                .validTime(groupBuyActivityResp.getValidTime())
                .status(groupBuyActivityResp.getStatus())
                .startTime(groupBuyActivityResp.getStartTime())
                .endTime(groupBuyActivityResp.getEndTime())
                .tagId(groupBuyActivityResp.getTagId())
                .tagScope(groupBuyActivityResp.getTagScope())
                .build();
        log.info("[活动仓储服务]查询到活动配置信息, groupBuyActivityDiscountVO:{}", groupBuyActivityDiscountVO);
        return groupBuyActivityDiscountVO;
    }

    @Override
    public SkuVO querySkuByGoodsId(String goodsId, String source, String channel) {
        Sku sku = skuDao.querySkuByGoodsId(Sku.builder().goodsId(goodsId).source(source).channel(channel).build());
        if (Objects.isNull(sku)) {
            log.error("[活动仓储服务]未查询到有效的商品信息, goodsId: {}, source: {}, channel: {}", goodsId, source, channel);
            return null;
        }
        SkuVO skuVO = SkuVO.builder()
                .goodsId(sku.getGoodsId())
                .goodsName(sku.getGoodsName())
                .originalPrice(sku.getOriginalPrice())
                .build();
        log.info("[活动仓储服务]查询到商品信息, skuVO:{}", skuVO);
        return skuVO;
    }

    @Override
    public SCSkuActivityVO queryActivityBySCGoodsId(String source, String channel, String goodsId) {
        // 根据SC和goodsId在关联表中查询活动Id
        SCSkuActivity scSkuActivityReq = SCSkuActivity.builder().source(source).channel(channel).goodsId(goodsId).build();
        SCSkuActivity activity = scSkuActivityDao.querySCSkuActivityBySCGoodsId(scSkuActivityReq);
        if (Objects.isNull(activity)) {
            log.error("[活动仓储服务]渠道商品活动表未查询到有效的活动Id, source:{}, channel:{}, goodsId:{}", source, channel, goodsId);
            return null;
        }
        SCSkuActivityVO scSkuActivity = SCSkuActivityVO.builder()
                .source(source)
                .channel(channel)
                .goodsId(goodsId)
                .activityId(activity.getActivityId())
                .build();
        log.info("[活动仓储服务]渠道商品活动表查询到拼团活动, scSkuActivity:{}", scSkuActivity);
        return scSkuActivity;
    }

    @Override
    public Boolean isTagCrowdRange(String userId, String tagId) {
        // 未登录用户，默认命中
        if (StringUtils.isBlank(userId)) {
            return true;
        }
        RBitSet bitSet = redisService.getBitSet(tagId);
        if (Objects.isNull(bitSet)) {
            log.info("[仓储服务]Redis中不存在标签：{}", tagId);
            return false;
        }
        boolean res = bitSet.get(redisService.getIndexFromUserId(userId));
        log.info("[仓储服务]Redis人群标签命中结果：用户：{} 标签：{} 是否命中：{}", userId, tagId, res);
        return res;
    }

    @Override
    public boolean isDowngrade() {
        return rccService.isDowngrade();
    }

    @Override
    public boolean isInCutFlowRange(String userId) {
        return rccService.isInCutFlowRange(userId);
    }

    @Override
    public boolean isInWhiteList(String userId) {
        return rccService.isInWhiteList(userId);
    }

    @Override
    public boolean isInWhiteList() {
        return rccService.isWhiteListMode();
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByOwner(String goodsId, String userId, Integer ownerCount) {
        // 1. 根据用户ID、商品ID，查询用户参与的拼团队伍
        GroupBuyOrderList groupBuyOrderListReq = new GroupBuyOrderList();
        groupBuyOrderListReq.setGoodsId(goodsId);
        groupBuyOrderListReq.setUserId(userId);
        groupBuyOrderListReq.setCount(ownerCount);
        List<GroupBuyOrderList> groupBuyOrderLists = groupBuyOrderListDao.queryInProgressUserGroupBuyOrderDetailListByUserId(groupBuyOrderListReq);
        if (null == groupBuyOrderLists || groupBuyOrderLists.isEmpty()) return null;

        // 2. 过滤队伍获取 TeamId
        Set<String> teamIds = groupBuyOrderLists.stream()
                .map(GroupBuyOrderList::getTeamId)
                .filter(teamId -> teamId != null && !teamId.isEmpty()) // 过滤非空和非空字符串
                .collect(Collectors.toSet());

        // 3. 查询队伍明细，组装Map结构
        List<GroupBuyOrder> groupBuyOrders = groupBuyOrderDao.queryGroupBuyProgressByTeamIds(teamIds);
        if (null == groupBuyOrders || groupBuyOrders.isEmpty()) return null;

        Map<String, GroupBuyOrder> groupBuyOrderMap = groupBuyOrders.stream()
                .collect(Collectors.toMap(GroupBuyOrder::getTeamId, order -> order));

        // 4. 转换数据
        List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities = new ArrayList<>();
        for (GroupBuyOrderList groupBuyOrderList : groupBuyOrderLists) {
            String teamId = groupBuyOrderList.getTeamId();
            GroupBuyOrder groupBuyOrder = groupBuyOrderMap.get(teamId);
            if (null == groupBuyOrder) continue;

            UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity = UserGroupBuyOrderDetailEntity.builder()
                    .userId(groupBuyOrderList.getUserId())
                    .teamId(groupBuyOrder.getTeamId())
                    .activityId(groupBuyOrder.getActivityId())
                    .targetCount(groupBuyOrder.getTargetCount())
                    .completeCount(groupBuyOrder.getCompleteCount())
                    .lockCount(groupBuyOrder.getLockCount())
                    .validStartTime(groupBuyOrder.getValidStartTime())
                    .validEndTime(groupBuyOrder.getValidEndTime())
                    .outTradeNo(groupBuyOrderList.getOutTradeNo())
                    .build();

            userGroupBuyOrderDetailEntities.add(userGroupBuyOrderDetailEntity);
        }

        return userGroupBuyOrderDetailEntities;
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByRandom(String goodsId, String userId, Integer randomCount) {
        // 1. 根据用户ID、活动ID，查询用户参与的拼团队伍
        GroupBuyOrderList groupBuyOrderListReq = new GroupBuyOrderList();
        groupBuyOrderListReq.setGoodsId(goodsId);
        groupBuyOrderListReq.setUserId(userId);
        groupBuyOrderListReq.setCount(randomCount * 2); // 查询2倍的量，之后其中 randomCount 数量
        List<GroupBuyOrderList> groupBuyOrderLists = groupBuyOrderListDao.queryInProgressUserGroupBuyOrderDetailListByRandom(groupBuyOrderListReq);
        if (null == groupBuyOrderLists || groupBuyOrderLists.isEmpty()) return null;

        // 判断总量是否大于 randomCount
        if (groupBuyOrderLists.size() > randomCount) {
            // 随机打乱列表
            Collections.shuffle(groupBuyOrderLists);
            // 获取前 randomCount 个元素
            groupBuyOrderLists = groupBuyOrderLists.subList(0, randomCount);
        }

        // 2. 过滤队伍获取 TeamId
        Set<String> teamIds = groupBuyOrderLists.stream()
                .map(GroupBuyOrderList::getTeamId)
                .filter(teamId -> teamId != null && !teamId.isEmpty()) // 过滤非空和非空字符串
                .collect(Collectors.toSet());

        // 3. 查询队伍明细，组装Map结构
        List<GroupBuyOrder> groupBuyOrders = groupBuyOrderDao.queryGroupBuyProgressByTeamIds(teamIds);
        if (null == groupBuyOrders || groupBuyOrders.isEmpty()) return null;

        Map<String, GroupBuyOrder> groupBuyOrderMap = groupBuyOrders.stream()
                .collect(Collectors.toMap(GroupBuyOrder::getTeamId, order -> order));

        // 4. 转换数据
        List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities = new ArrayList<>();
        for (GroupBuyOrderList groupBuyOrderList : groupBuyOrderLists) {
            String teamId = groupBuyOrderList.getTeamId();
            GroupBuyOrder groupBuyOrder = groupBuyOrderMap.get(teamId);
            if (null == groupBuyOrder) continue;

            UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity = UserGroupBuyOrderDetailEntity.builder()
                    .userId(groupBuyOrderList.getUserId())
                    .teamId(groupBuyOrder.getTeamId())
                    .activityId(groupBuyOrder.getActivityId())
                    .targetCount(groupBuyOrder.getTargetCount())
                    .completeCount(groupBuyOrder.getCompleteCount())
                    .lockCount(groupBuyOrder.getLockCount())
                    .validStartTime(groupBuyOrder.getValidStartTime())
                    .validEndTime(groupBuyOrder.getValidEndTime())
                    .build();

            userGroupBuyOrderDetailEntities.add(userGroupBuyOrderDetailEntity);
        }

        return userGroupBuyOrderDetailEntities;
    }

    @Override
    public TeamStatisticVO queryTeamStatisticByActivityId(String goodsId) {
        // 1. 根据活动ID查询拼团队伍
        List<GroupBuyOrderList> groupBuyOrderLists = groupBuyOrderListDao.queryInProgressUserGroupBuyOrderDetailListByGoodsId(goodsId);

        if (null == groupBuyOrderLists || groupBuyOrderLists.isEmpty()) {
            return new TeamStatisticVO(0, 0, 0);
        }

        // 2. 过滤队伍获取 TeamId
        Set<String> teamIds = groupBuyOrderLists.stream()
                .map(GroupBuyOrderList::getTeamId)
                .filter(teamId -> teamId != null && !teamId.isEmpty()) // 过滤非空和非空字符串
                .collect(Collectors.toSet());

        // 3. 统计数据
        Integer allTeamCount = groupBuyOrderDao.queryAllTeamCount(teamIds);
        Integer allTeamCompleteCount = groupBuyOrderDao.queryAllTeamCompleteCount(teamIds);
        Integer allTeamUserCount = groupBuyOrderDao.queryAllUserCount(teamIds);

        // 4. 构建对象
        return TeamStatisticVO.builder()
                .allTeamCount(allTeamCount)
                .allTeamCompleteCount(allTeamCompleteCount)
                .allTeamUserCount(allTeamUserCount)
                .build();
    }
}
