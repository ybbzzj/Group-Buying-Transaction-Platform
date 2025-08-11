package com.codezj.test.domain.activity.service;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.service.IIndexGroupBuyPlusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexGroupBuyPlusServiceTest {
    @Resource
    private IIndexGroupBuyPlusService indexGroupBuyPlusService;

    @Test
    public void testIndexGroupBuyPlusTrial() throws Exception {
        MarketProductEntity marketProductEntity = MarketProductEntity.builder()
                .userId("zhangsan")
                .goodsId("9890001")
                .source("s01")
                .channel("c01")
                .build();
        TrialBalanceEntity trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(marketProductEntity);
        log.info("优惠试算结果：trialBalanceEntity:{}", trialBalanceEntity);

        marketProductEntity = MarketProductEntity.builder()
                .userId("damuji")
                .goodsId("9890001")
                .source("s01")
                .channel("c01")
                .build();
        trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(marketProductEntity);
        log.info("优惠试算结果：trialBalanceEntity:{}", trialBalanceEntity);
    }

    @Test
    public void testErrorNode() throws Exception {
        MarketProductEntity marketProductEntity = MarketProductEntity.builder()
                .userId("zhangsan")
                .goodsId("9890001")
                .source("s01")
                .channel("c01")
                .build();
        TrialBalanceEntity trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(marketProductEntity);
        log.info("优惠试算结果：trialBalanceEntity:{}", trialBalanceEntity);
    }


}
