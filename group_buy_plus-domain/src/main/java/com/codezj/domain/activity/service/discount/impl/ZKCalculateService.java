package com.codezj.domain.activity.service.discount.impl;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service("ZK")
public class ZKCalculateService extends AbstractDiscountCalculateService {

    @Override
    protected BigDecimal doCalculate(BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception {
        String marketExpr = groupBuyDiscount.getMarketExpr();

        BigDecimal discount = new BigDecimal(marketExpr);

        BigDecimal payPrice = originPrice.multiply(discount);
        log.info("[优惠计算服务]折扣策略计算结果，原价: {}, 优惠后价格为: {}", originPrice, payPrice);
        return payPrice;
    }
}
