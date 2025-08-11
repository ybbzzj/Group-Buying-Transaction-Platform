package com.codezj.domain.trade.service.settlement.filter.impl;

import com.codezj.domain.trade.model.entity.MarketPayOrderEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterRespEntity;
import com.codezj.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.codezj.domain.trade.service.settlement.filter.AbstractSettlementRuleFilter;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 校验outTradeNo规则过滤
 **/
@Slf4j
@Service
public class OutTradeNoRuleFilter extends AbstractSettlementRuleFilter {


    @Override
    public SettlementRuleFilterRespEntity apply(SettlementRuleFilterParamsEntity params, TradeSettlementRuleFilterFactory.SettlementRuleFilterContext context) throws Exception {
        MarketPayOrderEntity marketPayOrder = repository.queryNotPayOrderByOutTradeNo(params.getUserId(), params.getOutTradeNo());
        if (Objects.isNull(marketPayOrder) || !TradeOrderStatusEnumVO.CREATE.equals(marketPayOrder.getTradeOrderStatusEnumVO())) {
            log.error("[结算规则过滤器] 用户待支付拼团订单不存在, 不需要结算。param:{}", params);
            throw new AppException(ResponseCode.S0102.getCode(), ResponseCode.S0102.getInfo());
        }

        // 上下文设置
        context.setMarketPayOrder(marketPayOrder);

        return next(params, context);
    }
}
