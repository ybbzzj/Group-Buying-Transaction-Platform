package com.codezj.domain.trade.model.entity;

import com.codezj.types.enums.GroupBuyStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 结算规则过滤结果实体
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRuleFilterRespEntity {
    /** 用户ID */
    private String userId;
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 外部交易单号 */
    private String outTradeNo;
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
    /** 目标数量 */
    private Integer targetCount;
    /** 完成数量 */
    private Integer completeCount;
    /** 锁单数量 */
    private Integer lockCount;
    /** 状态（0-拼单中、1-完成、2-失败） */
    private GroupBuyStatusEnum status;
    /** 回调地址 */
    private String notifyUrl;

}
