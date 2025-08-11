package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易活动实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayActivityEntity {
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
    /** 活动名称 */
    private String activityName;
    /** 拼团开始时间 */
    private LocalDateTime startTime;
    /** 拼团结束时间 */
    private LocalDateTime endTime;
    /** 目标数量 */
    private Integer targetCount;
    /** 外部交易单号-确保外部调用唯一幂等 */
    private String outTradeNo;
    /** 拼团持续时间 */
    private String validTime;
    /** 回调地址 */
    private String notifyUrl;
}
