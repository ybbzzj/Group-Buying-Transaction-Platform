package com.codezj.domain.activity.service.trial.node;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SkuVO;
import com.codezj.domain.activity.service.trial.AbstractGroupBuyPlusRouter;
import com.codezj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.codezj.types.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 结束节点
 */
@Slf4j
@Service
public class EndNode extends AbstractGroupBuyPlusRouter<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity>  {
    @Override
    public TrialBalanceEntity doApply(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        log.info("[拼团商品优惠试算服务] EndNode. 流程结束，即将返回结果...");

        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = context.getActivityDiscount();
        SkuVO skuVO = context.getSku();
        BigDecimal deductionPrice = context.getDeductionPrice();
        BigDecimal payPrice = context.getPayPrice();

        TrialBalanceEntity trialBalanceEntity = TrialBalanceEntity.builder()
                .goodsId(skuVO.getGoodsId())
                .goodsName(skuVO.getGoodsName())
                .originalPrice(skuVO.getOriginalPrice())
                .deductionPrice(deductionPrice)
                .payPrice(payPrice)
                .targetCount(groupBuyActivityDiscountVO.getTarget())
                .startTime(groupBuyActivityDiscountVO.getStartTime())
                .endTime(groupBuyActivityDiscountVO.getEndTime())
                .isVisible(context.getIsVisible())
                .isEnable(context.getIsEnable())
                .groupBuyActivityDiscountVO(groupBuyActivityDiscountVO)
                .build();
        log.info("[拼团商品优惠试算服务] EndNode. 优惠结果: {}", trialBalanceEntity);
        return trialBalanceEntity;
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> get(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        return null;
    }
}
