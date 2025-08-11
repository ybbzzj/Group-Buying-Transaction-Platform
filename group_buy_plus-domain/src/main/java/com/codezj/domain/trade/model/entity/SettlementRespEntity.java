package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 结算返回实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementRespEntity {

    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 用户ID */
    private String userId;
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
    /** 外部交易单号 */
    private String outTradeNo;
}
