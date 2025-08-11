package com.codezj.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static com.codezj.types.common.Constants.SPLIT;

/**
 * 拼单活动折扣信息VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyActivityDiscountVO {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 折扣信息
     */
    private GroupBuyDiscount groupBuyDiscount;

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
    private Integer status;

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

    /**
     * 活动是否可见
     * “1,2”，首位是1表示受标签限制
     */
    public boolean isVisible() {
        if (StringUtils.isBlank(tagScope)) {
            return true;
        }
        String[] split = tagScope.split(SPLIT);
        return split.length == 0 || !"1".equals(split[0]);
    }

    /**
     * 活动是否参与
     * “1,2”，第二位是2表示受标签限制
     */
    public boolean isEnable() {
        if (StringUtils.isBlank(tagScope)) {
            return true;
        }
        String[] split = tagScope.split(SPLIT);
        return split.length <= 1 || !"2".equals(split[1]);
    }


    /**
     * 活动折扣信息VO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupBuyDiscount {
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
    }


}
