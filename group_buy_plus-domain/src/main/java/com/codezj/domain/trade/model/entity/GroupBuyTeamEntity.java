package com.codezj.domain.trade.model.entity;

import com.codezj.types.enums.GroupBuyStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼单组队实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyTeamEntity {
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
    /**
     * 有效开始时间
     */
    private LocalDateTime validStartTime;
    /**
     * 有效结束时间
     */
    private LocalDateTime validEndTime;
    /** 回调地址 */
    private String notifyUrl;
}
