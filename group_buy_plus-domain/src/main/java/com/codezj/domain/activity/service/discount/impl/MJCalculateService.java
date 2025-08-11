package com.codezj.domain.activity.service.discount.impl;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.codezj.types.common.Constants.SPLIT;
import static com.codezj.types.common.Constants.MARKET_MINIMUM_PRICE;

/**
 * 满减优惠计算服务
 */
@Slf4j
@Service("MJ")
public class MJCalculateService extends AbstractDiscountCalculateService {

    @Override
    protected BigDecimal doCalculate(BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception {
        log.info("[优惠计算服务]满减策略，原价：{}", originPrice);

        // 获取折扣表达式
        String marketExpr = groupBuyDiscount.getMarketExpr();
        String[] split = marketExpr.split(SPLIT);
        BigDecimal threshold = new BigDecimal(split[0].trim());
        BigDecimal discount = new BigDecimal(split[1].trim());

        log.info("[优惠计算服务]满减策略: 满{}减{}", threshold, discount);

        // 未达到满减价格
        if (originPrice.compareTo(threshold) < 0) {
            log.info("[优惠计算服务]满减策略，未达到满减价格，优惠后价格：{}", originPrice);
            return originPrice;
        }

        BigDecimal payPrice = originPrice.subtract(discount);

        if (payPrice.compareTo(BigDecimal.ZERO) < 0) {
            log.info("[优惠计算服务]满减策略，优惠后价格小于0，优惠后价格：{}元", MARKET_MINIMUM_PRICE);
            return new BigDecimal(MARKET_MINIMUM_PRICE);
        }

        log.info("[优惠计算服务]满减策略，优惠后价格：{}", payPrice);
        return payPrice;


    }
}
