package com.codezj.test.domain.trade;

import com.alibaba.fastjson.JSON;
import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.service.IIndexGroupBuyPlusService;
import com.codezj.domain.trade.model.entity.MarketPayOrderEntity;
import com.codezj.domain.trade.model.entity.PayActivityEntity;
import com.codezj.domain.trade.model.entity.PayDiscountEntity;
import com.codezj.domain.trade.model.entity.UserEntity;
import com.codezj.domain.trade.service.ITradeLockOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼图交易锁单测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TradeServiceTest {

    @Resource
    private ITradeLockOrderService tradeService;

    @Resource
    private IIndexGroupBuyPlusService indexGroupBuyPlusService;


    @Test
    public void testQueryNotPayOrderByOutTradeNo() {
        MarketPayOrderEntity order = tradeService.queryNotPayOrderByOutTradeNo("lisi", "345342456644");
        log.info("测试结果：order:{}", order);
    }

    @Test
    public void testCreateGroupBuyOrder() throws Exception {
        // 入参信息
        Long activityId = 100124L;
        String userId = "jiji";
        String goodsId = "9890001";
        String source = "s01";
        String channel = "c01";
        String notifyUrl = "http://127.0.0.1:8091/api/v1/test/group_buy_notify";
        String outTradeNo = RandomStringUtils.randomNumeric(12);

        // 1. 获取试算优惠，有【activityId】优先使用
        TrialBalanceEntity trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(MarketProductEntity.builder()
                .userId(userId)
                .source(source)
                .channel(channel)
                .goodsId(goodsId)
                .activityId(activityId)
                .build());

        if (!trialBalanceEntity.getIsEnable()) {
            log.info("测试结果：用户{}没有拼团资格",userId);
        }

        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();

        // 2. 锁定，营销预支付订单；商品下单前，预购锁定。
        MarketPayOrderEntity marketPayOrderEntityNew = tradeService.lockGroupBuyTradeOrder(
                UserEntity.builder().userId(userId).build(),
                PayActivityEntity.builder()
                        .teamId(null)
                        .activityId(groupBuyActivityDiscountVO.getActivityId())
                        .activityName(groupBuyActivityDiscountVO.getActivityName())
                        .startTime(groupBuyActivityDiscountVO.getStartTime())
                        .endTime(groupBuyActivityDiscountVO.getEndTime())
                        .targetCount(groupBuyActivityDiscountVO.getTarget())
                        .outTradeNo(outTradeNo)
                        .notifyUrl(notifyUrl)
                        .validTime(groupBuyActivityDiscountVO.getValidTime())
                        .build(),
                PayDiscountEntity.builder()
                        .source(source)
                        .channel(channel)
                        .goodsId(goodsId)
                        .goodsName(trialBalanceEntity.getGoodsName())
                        .originalPrice(trialBalanceEntity.getOriginalPrice())
                        .deductionPrice(trialBalanceEntity.getDeductionPrice())
                        .payPrice(trialBalanceEntity.getPayPrice())
                        .build());

        log.info("测试结果(New):{}",JSON.toJSONString(marketPayOrderEntityNew));
    }

    @Test
    public void testJoinGroupBuyOrder() throws Exception {
        // 入参信息
        Long activityId = 100124L;
        String userId = "ooii";
        String goodsId = "9890001";
        String source = "s01";
        String channel = "c01";
        String outTradeNo = RandomStringUtils.randomNumeric(12);
        String teamId = "30255039";

        // 1. 获取试算优惠，有【activityId】优先使用
        TrialBalanceEntity trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(MarketProductEntity.builder()
                .userId(userId)
                .source(source)
                .channel(channel)
                .goodsId(goodsId)
                .activityId(activityId)
                .build());

        if (!trialBalanceEntity.getIsEnable()) {
            log.info("测试结果：用户{}没有拼团资格",userId);
            return;
        }

        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();


        // 2. 锁定，营销预支付订单；商品下单前，预购锁定。
        MarketPayOrderEntity marketPayOrderEntityNew = tradeService.lockGroupBuyTradeOrder(
                UserEntity.builder().userId(userId).build(),
                PayActivityEntity.builder()
                        .teamId(teamId)
                        .activityId(groupBuyActivityDiscountVO.getActivityId())
                        .activityName(groupBuyActivityDiscountVO.getActivityName())
                        .startTime(groupBuyActivityDiscountVO.getStartTime())
                        .endTime(groupBuyActivityDiscountVO.getEndTime())
                        .targetCount(groupBuyActivityDiscountVO.getTarget())
                        .outTradeNo(outTradeNo)
                        .build(),
                PayDiscountEntity.builder()
                        .source(source)
                        .channel(channel)
                        .goodsId(goodsId)
                        .goodsName(trialBalanceEntity.getGoodsName())
                        .originalPrice(trialBalanceEntity.getOriginalPrice())
                        .deductionPrice(trialBalanceEntity.getDeductionPrice())
                        .build());


        log.info("测试结果(New):{}",JSON.toJSONString(marketPayOrderEntityNew));
    }
}
