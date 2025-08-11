package com.codezj.domain.trade.service;

import com.codezj.domain.trade.model.entity.MarketPayOrderEntity;
import com.codezj.domain.trade.model.entity.PayActivityEntity;
import com.codezj.domain.trade.model.entity.PayDiscountEntity;
import com.codezj.domain.trade.model.entity.UserEntity;
import com.codezj.domain.trade.model.valobj.TradeOrderProgressVO;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易领域服务
 **/
public interface ITradeLockOrderService {

    /**
     * 根据outTradeNo查询用户尚未支付的Ï订单
     *
     * @param userId     用户ID
     * @param outTradeNo 外部交易（唯一幂等）
     * @return 订单实体
     */
    MarketPayOrderEntity queryNotPayOrderByOutTradeNo(String userId, String outTradeNo);

    /**
     * 查询交易订单进度
     *
     * @param teamId 拼单ID
     * @return 交易订单进度
     */
    TradeOrderProgressVO queryTradeOrderProgress(String teamId);

    /**
     * 锁定拼团交易订单
     *
     * @param userEntity        用户实体
     * @param payActivityEntity 支付活动实体
     * @param payDiscountEntity 支付优惠实体
     * @return 支付订单实体
     */
    MarketPayOrderEntity lockGroupBuyTradeOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception;
}
