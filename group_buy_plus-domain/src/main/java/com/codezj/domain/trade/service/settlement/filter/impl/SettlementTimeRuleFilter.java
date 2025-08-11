package com.codezj.domain.trade.service.settlement.filter.impl;

import com.codezj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterRespEntity;
import com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.codezj.domain.trade.service.settlement.filter.AbstractSettlementRuleFilter;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团时间规则过滤
 **/
@Slf4j
@Service
public class SettlementTimeRuleFilter extends AbstractSettlementRuleFilter {

    @Override
    public SettlementRuleFilterRespEntity apply(SettlementRuleFilterParamsEntity params, TradeSettlementRuleFilterFactory.SettlementRuleFilterContext context) throws Exception {

        GroupBuyTeamEntity groupBuyTeamEntity = context.getGroupBuyTeam();


        if (params.getOutTradeTime().isBefore(groupBuyTeamEntity.getValidStartTime()) ||
            params.getOutTradeTime().isAfter(groupBuyTeamEntity.getValidEndTime())) {
            log.error("[结算规则过滤器] 结算时间不在订单待支付有效时间内, 结算被拦截。结算时间：{}, 有效时间：{}-{}",
                    params.getOutTradeTime(), groupBuyTeamEntity.getValidStartTime(),groupBuyTeamEntity.getValidEndTime());
            throw new AppException(ResponseCode.S0103.getCode(), ResponseCode.S0103.getInfo());
        }


        return next(params, context);
    }
}
