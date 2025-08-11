package com.codezj.domain.trade.model.entity;

import com.codezj.types.enums.ActivityStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团活动实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyActivityEntity {
    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 折扣ID
     */
    private Long discountId;

    /**
     * 拼团方式（0自动成团、1达成目标拼团）
     */
    private Integer groupType;

    /**
     * 拼团次数限制
     */
    private Integer takeLimitCount;

    /**
     * 拼团目标
     */
    private Integer target;

    /**
     * 拼团时长（分钟）
     */
    private String validTime;

    /**
     * 活动状态（0创建、1生效、2过期、3废弃）
     */
    private ActivityStatusEnum status;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 人群标签规则标识
     */
    private String tagId;

    /**
     * 人群标签规则范围（多选；1可见限制、2参与限制）
     */
    private String tagScope;
}
