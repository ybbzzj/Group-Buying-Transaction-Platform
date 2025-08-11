package com.codezj.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author baozhongjie
 * @desription 折扣信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyDiscount {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 折扣ID
     */
    private Long discountId;

    /**
     * 折扣标题
     */
    private String discountName;

    /**
     * 折扣描述
     */
    private String discountDesc;

    /**
     * 折扣类型（0:base、1:tag）
     */
    private Integer discountType;

    /**
     * 营销优惠计划（ZJ:直减、MJ:满减、N元购）
     */
    private String marketPlan;

    /**
     * 营销优惠表达式
     */
    private String marketExpr;

    /**
     * 人群标签，特定优惠限定
     */
    private String tagId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
