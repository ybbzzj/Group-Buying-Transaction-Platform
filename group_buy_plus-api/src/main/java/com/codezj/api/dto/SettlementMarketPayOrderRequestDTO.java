package com.codezj.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 拼单结算请求DTO
 **/
@Data
public class SettlementMarketPayOrderRequestDTO {

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
