package com.codezj.domain.trade.service.settlement.filter;

import com.codezj.domain.trade.adapter.repository.ITradeRepository;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterRespEntity;
import com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.codezj.types.design.framework.link.multition.handler.ILogicHandler;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class AbstractSettlementRuleFilter implements
        ILogicHandler<SettlementRuleFilterParamsEntity,
                TradeSettlementRuleFilterFactory.SettlementRuleFilterContext, SettlementRuleFilterRespEntity> {

    @Resource
    protected ITradeRepository repository;

    @Override
    public SettlementRuleFilterRespEntity apply(SettlementRuleFilterParamsEntity params, TradeSettlementRuleFilterFactory.SettlementRuleFilterContext context) throws Exception {
        return null;
    }
}
