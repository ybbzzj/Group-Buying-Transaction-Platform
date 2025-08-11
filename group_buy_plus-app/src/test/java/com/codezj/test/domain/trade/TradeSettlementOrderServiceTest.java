package com.codezj.test.domain.trade;

import com.codezj.domain.trade.model.entity.SettlementReqEntity;
import com.codezj.domain.trade.model.entity.SettlementRespEntity;
import com.codezj.domain.trade.service.settlement.TradeSettlementOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 结算单服务测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TradeSettlementOrderServiceTest {

    @Resource
    private TradeSettlementOrderService tradeSettlementOrderService;

    @Test
    public void testExecuteSettlement() throws Exception {
        SettlementReqEntity settlementReqEntity = SettlementReqEntity.builder()
                .userId("ooii")
                .source("s01")
                .channel("c01")
                .outTradeNo("177982522379")
                .outTradeTime(LocalDateTime.now())
                .build();
        SettlementRespEntity settlementRespEntity = tradeSettlementOrderService.executeSettlement(settlementReqEntity);
        log.info("测试结果:{}", settlementRespEntity);
    }
}
