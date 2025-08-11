package com.codezj.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 商品活动关联信息
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCSkuActivityVO {
    /**
     * 来源
     */
    private String source;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 商品ID
     */
    private String goodsId;
}
