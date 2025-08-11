package com.codezj.domain.trade.service.settlement.factory;

import com.codezj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.codezj.domain.trade.model.entity.MarketPayOrderEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterRespEntity;
import com.codezj.domain.trade.service.settlement.filter.impl.*;
import com.codezj.types.design.framework.link.multition.LinkArmory;
import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易结算规则过滤工厂类
 **/
@Slf4j
@Service
public class TradeSettlementRuleFilterFactory {

    private static final String SETTLEMENT_RULE_FILTER_CHAIN = "结算规则责任链";

    @Bean("settlementRuleFilterChain")
    public BusinessLinkedList<SettlementRuleFilterParamsEntity,
            SettlementRuleFilterContext, SettlementRuleFilterRespEntity> settlementRuleFilterChain(SCRuleFilter scruleFilter,
                                                                                              SettlementTimeRuleFilter settlementTimeRuleFilter,
                                                                                              GroupTeamRuleFilter groupTeamRuleFilter,
                                                                                              OutTradeNoRuleFilter outTradeNoRuleFilter,
                                                                                              EndRuleFilter endRuleFilter) {
        LinkArmory<SettlementRuleFilterParamsEntity,
                SettlementRuleFilterContext, SettlementRuleFilterRespEntity> linkArmory = new LinkArmory<>(SETTLEMENT_RULE_FILTER_CHAIN, scruleFilter, outTradeNoRuleFilter, groupTeamRuleFilter, settlementTimeRuleFilter, endRuleFilter);
        log.info("[责任链工厂] {} 装配完成", SETTLEMENT_RULE_FILTER_CHAIN);
        return linkArmory.getLogicLink();
    }




    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SettlementRuleFilterContext {
        // 结算规则过滤上下文
        private MarketPayOrderEntity marketPayOrder;
        private GroupBuyTeamEntity groupBuyTeam;
    }
}
