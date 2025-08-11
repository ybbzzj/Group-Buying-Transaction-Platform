package com.codezj.domain.trade.service.settlement.filter.impl;

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
 * @Description: 结算规则过滤（SC黑名单）
 **/
@Slf4j
@Service
public class SCRuleFilter extends AbstractSettlementRuleFilter {
    @Override
    public SettlementRuleFilterRespEntity apply(SettlementRuleFilterParamsEntity params, TradeSettlementRuleFilterFactory.SettlementRuleFilterContext context) throws Exception {
        boolean inSCBlockList = repository.isInSCBlockList(params.getSource(), params.getChannel());
        if (inSCBlockList) {
            log.error("[结算规则过滤器] 命中了SC黑名单, 结算被拦截");
            throw new AppException(ResponseCode.S0101.getCode(), ResponseCode.S0101.getInfo());
        }

        return next(params, context);
    }
}
