package com.codezj.domain.trade.service.settlement.filter.impl;

import com.codezj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.codezj.domain.trade.model.entity.MarketPayOrderEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.SettlementRuleFilterRespEntity;
import com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.codezj.domain.trade.service.settlement.filter.AbstractSettlementRuleFilter;
import com.codezj.types.enums.GroupBuyStatusEnum;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团合法性规则过滤
 **/
@Slf4j
@Service
public class GroupTeamRuleFilter extends AbstractSettlementRuleFilter {

    @Override
    public SettlementRuleFilterRespEntity apply(SettlementRuleFilterParamsEntity params, TradeSettlementRuleFilterFactory.SettlementRuleFilterContext context) throws Exception {
        MarketPayOrderEntity marketPayOrder = context.getMarketPayOrder();

        GroupBuyTeamEntity groupBuyTeamEntity = repository.queryGroupBuyTeam(marketPayOrder.getTeamId());

        if (Objects.isNull(groupBuyTeamEntity) || !GroupBuyStatusEnum.PROGRESS.equals(groupBuyTeamEntity.getStatus())) {
            log.error("[结算规则过滤器] 拼团不存在或已解散, 不需要结算。param:{}", params);
            throw new AppException(ResponseCode.S0104.getCode(), ResponseCode.S0104.getInfo());
        }

        context.setGroupBuyTeam(groupBuyTeamEntity);

        return next(params, context);
    }
}
