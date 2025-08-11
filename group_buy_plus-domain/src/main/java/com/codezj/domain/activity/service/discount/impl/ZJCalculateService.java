package com.codezj.domain.activity.service.discount.impl;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.codezj.types.common.Constants.MARKET_MINIMUM_PRICE;

@Slf4j
@Service("ZJ")
public class ZJCalculateService extends AbstractDiscountCalculateService {

    @Override
    protected BigDecimal doCalculate(BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception {
        String marketExpr = groupBuyDiscount.getMarketExpr();

        BigDecimal payPrice = originPrice.subtract(new BigDecimal(marketExpr));

        if (payPrice.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("[优惠计算服务]直减策略，折扣金额大于商品原价, 商品原价:{}, 折扣金额:{}", originPrice, MARKET_MINIMUM_PRICE);
            return new BigDecimal(MARKET_MINIMUM_PRICE);
        }

        log.info("[优惠计算服务]直减策略，折扣计算, 商品原价:{}, 折扣金额:{}", originPrice, payPrice);
        return payPrice;
    }
}
