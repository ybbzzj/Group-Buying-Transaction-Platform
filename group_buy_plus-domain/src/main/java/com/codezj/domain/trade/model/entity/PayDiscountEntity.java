package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易折扣实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayDiscountEntity {

    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 商品ID */
    private String goodsId;
    /** 商品名称 */
    private String goodsName;
    /** 原始价格 */
    private BigDecimal originalPrice;
    /** 折扣金额 */
    private BigDecimal deductionPrice;
    /** 支付价格 */
    private BigDecimal payPrice;

}
