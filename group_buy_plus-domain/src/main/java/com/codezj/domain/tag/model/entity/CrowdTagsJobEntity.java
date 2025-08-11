package com.codezj.domain.tag.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 人群标签任务
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrowdTagsJobEntity {

    /** 标签类型（参与量、消费金额） */
    private Integer tagType;
    /** 标签规则（限定类型 N次） */
    private String tagRule;
    /** 统计数据，开始时间 */
    private LocalDateTime statStartTime;
    /** 统计数据，结束时间 */
    private LocalDateTime statEndTime;
    /** 状态；0初始、1计划（进入执行阶段）、2重置、3完成 */
    private Integer status;
}
