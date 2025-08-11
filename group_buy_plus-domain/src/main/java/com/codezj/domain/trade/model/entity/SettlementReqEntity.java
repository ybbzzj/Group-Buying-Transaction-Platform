package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 结算入参实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementReqEntity {


    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 用户ID */
    private String userId;
    /** 外部交易单号 */
    private String outTradeNo;
    /** 外部交易时间 */
    private LocalDateTime outTradeTime;
}
