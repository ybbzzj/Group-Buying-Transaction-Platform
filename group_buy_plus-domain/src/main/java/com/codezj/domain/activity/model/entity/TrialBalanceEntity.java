package com.codezj.domain.activity.model.entity;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 试算结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrialBalanceEntity {
    /** 商品ID */
    private String goodsId;
    /** 商品名称 */
    private String goodsName;
    /** 原始价格 */
    private BigDecimal originalPrice;
    /** 折扣价格 */
    private BigDecimal deductionPrice;
    /** 支付价格 */
    private BigDecimal payPrice;
    /** 拼团目标数量 */
    private Integer targetCount;
    /** 拼团开始时间 */
    private LocalDateTime startTime;
    /** 拼团结束时间 */
    private LocalDateTime endTime;
    /** 是否可见拼团 */
    private Boolean isVisible;
    /** 是否可参与进团 */
    private Boolean isEnable;

    /** 活动配置信息 */
    private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;

    @Override
    public String toString() {
        return "商品ID：" + goodsId + "\n" +
                "商品名称：" + goodsName + "\n" +
                "原始价格：" + originalPrice + "\n" +
                "折扣：" + deductionPrice + "\n" +
                "待支付价格：" + payPrice + "\n" +
                "拼团目标数量：" + targetCount + "\n" +
                "拼团开始时间：" + startTime + "\n" +
                "拼团结束时间：" + endTime + "\n" +
                "是否可见拼团：" + isVisible + "\n" +
                "是否可参与拼团：" + isEnable;
    }
}
