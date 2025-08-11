package com.codezj.domain.trade.model.entity;

import com.codezj.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 支付订单实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPayOrderEntity {
    /** 拼团ID */
    private String teamId;
    /** 预购订单ID */
    private String orderId;
    /** 折扣金额 */
    private BigDecimal deductionPrice;
    /** 交易订单状态枚举 */
    private TradeOrderStatusEnumVO tradeOrderStatusEnumVO;
}
