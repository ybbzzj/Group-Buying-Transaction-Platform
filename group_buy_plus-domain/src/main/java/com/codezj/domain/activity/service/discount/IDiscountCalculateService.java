package com.codezj.domain.activity.service.discount;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.math.BigDecimal;

/**
 * 营销优惠计算接口
 */
public interface IDiscountCalculateService {

    /**
     * 计算优惠金额
     *
     * @param userId           用户id
     * @param originPrice      商品原价
     * @param groupBuyDiscount 折扣配置
     * @return 优惠后金额
     * @throws Exception 计算异常
     */
    BigDecimal calculateDiscount(String userId, BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception;
}
