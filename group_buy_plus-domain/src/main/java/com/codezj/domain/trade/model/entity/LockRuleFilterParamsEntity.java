package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 规则过滤入参实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockRuleFilterParamsEntity {
    /** 用户ID */
    private String userId;

    /** 活动ID */
    private Long activityId;
}
