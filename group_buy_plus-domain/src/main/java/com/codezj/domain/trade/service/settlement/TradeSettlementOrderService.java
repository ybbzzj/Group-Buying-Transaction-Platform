package com.codezj.domain.trade.service.settlement;

import com.codezj.domain.trade.adapter.port.ITradePort;
import com.codezj.domain.trade.adapter.repository.ITradeRepository;
import com.codezj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.codezj.domain.trade.model.entity.*;
import com.codezj.domain.trade.model.valobj.NotifyTaskHTTPEnumVO;
import com.codezj.domain.trade.service.ITradeSettlementOrderService;
import com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易结算服务
 **/
@Slf4j
@Service
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    @Resource
    private ITradeRepository repository;

    @Resource
    private ITradePort port;

    @Resource
    private BusinessLinkedList<SettlementRuleFilterParamsEntity,
            TradeSettlementRuleFilterFactory.SettlementRuleFilterContext, SettlementRuleFilterRespEntity> settlementRuleFilterChain;

    @Override
    public SettlementRespEntity executeSettlement(SettlementReqEntity settlementReqEntity) throws Exception {
        // 执行结算规则
        SettlementRuleFilterRespEntity filterResp = settlementRuleFilterChain.apply(new SettlementRuleFilterParamsEntity(settlementReqEntity.getUserId(),
                settlementReqEntity.getSource(), settlementReqEntity.getChannel(), settlementReqEntity.getOutTradeNo(),
                LocalDateTime.now()), new TradeSettlementRuleFilterFactory.SettlementRuleFilterContext());
        UserEntity userEntity = UserEntity.builder().userId(filterResp.getUserId()).build();
        GroupBuyTeamEntity groupBuyTeamEntity = GroupBuyTeamEntity.builder()
                .teamId(filterResp.getTeamId())
                .activityId(filterResp.getActivityId())
                .status(filterResp.getStatus())
                .targetCount(filterResp.getTargetCount())
                .completeCount(filterResp.getCompleteCount())
                .lockCount(filterResp.getLockCount())
                .notifyUrl(filterResp.getNotifyUrl())
                .build();
        // 聚合对象
        GroupBuyTeamSettlementAggregate groupBuyTeamSettlementAggregate = new GroupBuyTeamSettlementAggregate(userEntity,
                groupBuyTeamEntity, settlementReqEntity);

        // 执行结算
        repository.settleGroupBuyOrder(groupBuyTeamSettlementAggregate);

        Map<String, Integer> notifyJob = execSettlementNotifyJob(groupBuyTeamEntity.getTeamId());
        log.info("[结算服务] -执行结算通知任务，结果:{}", notifyJob);

        // 构建响应对象
        SettlementRespEntity respEntity = SettlementRespEntity.builder()
                .userId(settlementReqEntity.getUserId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .teamId(groupBuyTeamEntity.getTeamId())
                .channel(settlementReqEntity.getChannel())
                .source(settlementReqEntity.getSource())
                .outTradeNo(settlementReqEntity.getOutTradeNo())
                .build();
        log.info("[结算服务] 用户订单结算完成, respEntity:{}", respEntity);
        return respEntity;
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob() throws Exception {
        log.info("[结算服务] -执行结算通知任务");

        // 查询未执行任务
        List<NotifyTaskEntity> notifyTaskEntityList = repository.queryUnExecutedNotifyTaskList();

        return execSettlementNotifyJob(notifyTaskEntityList);
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob(String teamId) throws Exception {
        log.info("[结算服务] -执行结算通知任务，指定teamId:{}", teamId);

        // 查询未执行任务
        List<NotifyTaskEntity> notifyTaskEntityList = repository.queryUnExecutedNotifyTaskList(teamId);

        return execSettlementNotifyJob(notifyTaskEntityList);
    }

    private Map<String, Integer> execSettlementNotifyJob(List<NotifyTaskEntity> notifyTaskEntityList) throws Exception {
        Map<String, Integer> result = new HashMap<>();
        int successCount = 0;
        int errorCount = 0;
        int retryCount = 0;
        for (NotifyTaskEntity notifyTask : notifyTaskEntityList) {
            String response = port.groupBuyNotify(notifyTask);
            // 更新状态判断&变更数据库表回调任务状态
            if (NotifyTaskHTTPEnumVO.SUCCESS.getCode().equals(response)) {
                int updateCount = repository.updateNotifyTaskStatusSuccess(notifyTask.getTeamId());
                if (1 == updateCount) {
                    successCount += 1;
                }
            } else if (NotifyTaskHTTPEnumVO.ERROR.getCode().equals(response)) {
                if (notifyTask.getNotifyCount() < 5) {
                    int updateCount = repository.updateNotifyTaskStatusError(notifyTask.getTeamId());
                    if (1 == updateCount) {
                        errorCount += 1;
                    }
                } else {
                    int updateCount = repository.updateNotifyTaskStatusRetry(notifyTask.getTeamId());
                    if (1 == updateCount) {
                        retryCount += 1;
                    }
                }
            }
        }
        result.put("waitCount", notifyTaskEntityList.size());
        result.put("success", successCount);
        result.put("error", errorCount);
        result.put("retry", retryCount);
        return result;
    }
}
