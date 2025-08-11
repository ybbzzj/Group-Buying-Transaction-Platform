package com.codezj.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 锁定支付订单
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockMarketPayOrderRequestDTO {

    // 用户ID
    private String userId;
    // 拼单组队ID - 可为空，为空则创建新组队ID
    private String teamId;
    // 活动ID
    private Long activityId;
    // 商品ID
    private String goodsId;
    // 渠道
    private String source;
    // 来源
    private String channel;
    // 外部交易单号
    private String outTradeNo;
    // 回调地址
    private String notifyUrl;

}
