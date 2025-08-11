package com.codezj.domain.trade.model.aggregate;

import com.codezj.domain.trade.model.entity.PayActivityEntity;
import com.codezj.domain.trade.model.entity.PayDiscountEntity;
import com.codezj.domain.trade.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团订单聚合
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyOrderAggregate {

    /**
     * 支付活动实体
     */
    private PayActivityEntity payActivityEntity;

    /**
     * 用户实体
     */
    private UserEntity userEntity;

    /**
     * 支付折扣实体
     */
    private PayDiscountEntity payDiscountEntity;

    /**
     * 用户参与活动的次数
     */
    private Integer userTakeCount;
}
