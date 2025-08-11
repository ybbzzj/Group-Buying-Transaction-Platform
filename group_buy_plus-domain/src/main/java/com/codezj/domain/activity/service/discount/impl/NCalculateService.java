package com.codezj.domain.activity.service.discount.impl;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * N元优惠计算服务，直接优惠至N元
 */
@Slf4j
@Service("N")
public class NCalculateService extends AbstractDiscountCalculateService {

    @Override
    protected BigDecimal doCalculate(BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception {
        String payPrice = groupBuyDiscount.getMarketExpr();
        BigDecimal payPriceDecimal = new BigDecimal(payPrice);
        log.info("[优惠计算服务]N元购买优惠, 原价: {}, 优惠后价格: {}", originPrice, payPrice);
        return payPriceDecimal;
    }
}
