package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 规则过滤返回实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockRuleFilterRespEntity {

    /**
     * 用户参与活动的次数
     */
    private Integer userTakeCount;
}
