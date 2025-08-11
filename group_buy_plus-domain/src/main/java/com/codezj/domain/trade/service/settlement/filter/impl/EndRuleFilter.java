package com.codezj.domain.trade.service.settlement.filter.impl;

import com.codezj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterRespEntity;
import com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.codezj.domain.trade.service.settlement.filter.AbstractSettlementRuleFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Slf4j
@Service
public class EndRuleFilter extends AbstractSettlementRuleFilter {

    @Override
    public SettlementRuleFilterRespEntity apply(SettlementRuleFilterParamsEntity params, TradeSettlementRuleFilterFactory.SettlementRuleFilterContext context) throws Exception {
        GroupBuyTeamEntity groupBuyTeam = context.getGroupBuyTeam();

        SettlementRuleFilterRespEntity settlementRuleFilterRespEntity = SettlementRuleFilterRespEntity.builder()
                .activityId(groupBuyTeam.getActivityId())
                .teamId(groupBuyTeam.getTeamId())
                .userId(params.getUserId())
                .source(params.getSource())
                .channel(params.getChannel())
                .status(groupBuyTeam.getStatus())
                .outTradeNo(params.getOutTradeNo())
                .targetCount(groupBuyTeam.getTargetCount())
                .completeCount(groupBuyTeam.getCompleteCount())
                .lockCount(groupBuyTeam.getLockCount())
                .notifyUrl(groupBuyTeam.getNotifyUrl())
                .build();

        log.info("[结算规则过滤器] 执行完成，返回过滤结果。settlementRuleFilterRespEntity:{}", settlementRuleFilterRespEntity);
        return settlementRuleFilterRespEntity;
    }
}
