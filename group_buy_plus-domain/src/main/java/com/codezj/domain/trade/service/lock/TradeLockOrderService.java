package com.codezj.domain.trade.service.lock;

import com.codezj.domain.trade.adapter.repository.ITradeRepository;
import com.codezj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.codezj.domain.trade.model.entity.*;
import com.codezj.domain.trade.model.valobj.TradeOrderProgressVO;
import com.codezj.domain.trade.service.ITradeLockOrderService;
import com.codezj.domain.trade.service.lock.factory.TraderLockRuleFilterFactory;
import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易领域服务
 **/
@Slf4j
@Service
public class TradeLockOrderService implements ITradeLockOrderService {

    @Resource
    private ITradeRepository repository;

    @Resource
    private BusinessLinkedList<LockRuleFilterParamsEntity, TraderLockRuleFilterFactory.LockRuleFilterContext, LockRuleFilterRespEntity> tradeRuleFilterChain;

    @Override
    public MarketPayOrderEntity queryNotPayOrderByOutTradeNo(String userId, String outTradeNo) {
        log.info("[交易服务] 查询用户{}未支付订单{}...", userId, outTradeNo);
        return repository.queryNotPayOrderByOutTradeNo(userId, outTradeNo);
    }

    @Override
    public TradeOrderProgressVO queryTradeOrderProgress(String teamId) {
        log.info("[交易服务] 查询拼团订单，拼团号：{}...", teamId);
        return repository.queryTradeOrderProgress(teamId);
    }

    @Override
    public MarketPayOrderEntity lockGroupBuyTradeOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception {
        log.info("[交易服务] 正在锁定拼团交易订单，请稍后...");

        // 规则过滤（责任链）
        LockRuleFilterRespEntity ruleFilterRespEntity = tradeRuleFilterChain.apply(LockRuleFilterParamsEntity.builder()
                        .userId(userEntity.getUserId())
                        .activityId(payActivityEntity.getActivityId())
                        .build(),
                new TraderLockRuleFilterFactory.LockRuleFilterContext());

        // 聚合拼单信息对象
        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .userEntity(userEntity)
                .payActivityEntity(payActivityEntity)
                .payDiscountEntity(payDiscountEntity)
                .userTakeCount(ruleFilterRespEntity.getUserTakeCount())
                .build();

        // 锁定拼团交易订单
        MarketPayOrderEntity order = repository.lockGroupBuyTradeOrder(groupBuyOrderAggregate);
        log.info("[交易服务] 拼团交易订单锁定成功! 订单号：{}", order.getOrderId());
        return order;
    }
}
