package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 结算规则过滤参数
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRuleFilterParamsEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 来源
     */
    private String source;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 外部交易号
     */
    private String outTradeNo;
    /**
     * 外部交易时间
     */
    private LocalDateTime outTradeTime;



}
