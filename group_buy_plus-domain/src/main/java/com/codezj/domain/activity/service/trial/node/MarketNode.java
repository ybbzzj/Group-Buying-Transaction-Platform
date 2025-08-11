package com.codezj.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SkuVO;
import com.codezj.domain.activity.service.discount.AbstractDiscountCalculateService;
import com.codezj.domain.activity.service.trial.AbstractGroupBuyPlusRouter;
import com.codezj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.codezj.types.design.framework.tree.StrategyHandler;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 拼单处理节点
 */
@Slf4j
@Service
public class MarketNode extends AbstractGroupBuyPlusRouter<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> {

    @Resource
    private EndNode endNode;

    @Resource
    private Map<String, AbstractDiscountCalculateService> discountCalculateServiceMap;


    @Override
    public TrialBalanceEntity doApply(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) throws Exception {
        log.info("[拼团商品优惠试算服务]MarketNode. 开始处理业务流程...");


        GroupBuyActivityDiscountVO activityDiscount = context.getActivityDiscount();
        SkuVO sku = context.getSku();
        String marketPlan = activityDiscount.getGroupBuyDiscount().getMarketPlan();

        // 拼团优惠试算
        AbstractDiscountCalculateService discountCalculateService = discountCalculateServiceMap.get(marketPlan);
        if (Objects.isNull(discountCalculateService)) {
            log.error("[拼团商品优惠试算服务]MarketNode. 暂不支持{}类型的优惠试算服务，支持的类型有：{}", marketPlan, JSON.toJSONString(discountCalculateServiceMap.keySet()));
            throw new AppException(ResponseCode.E0001.getCode(), ResponseCode.E0001.getInfo());
        }
        BigDecimal payPrice = discountCalculateService.calculateDiscount(params.getUserId(), sku.getOriginalPrice(), activityDiscount.getGroupBuyDiscount());
        context.setDeductionPrice(sku.getOriginalPrice().subtract(payPrice));
        context.setPayPrice(payPrice);
        log.info("[拼团商品优惠试算服务]MarketNode. 优惠试算成功！");
        return route(params, context);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> get(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        return endNode;
    }
}
