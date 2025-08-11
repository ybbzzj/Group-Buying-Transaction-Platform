package com.codezj.test.integration;

import com.codezj.api.IMarketTradeService;
import com.codezj.api.dto.LockMarketPayOrderRequestDTO;
import com.codezj.api.dto.LockMarketPayOrderResponseDTO;
import com.codezj.api.response.Response;
import com.codezj.domain.trade.model.entity.SettlementReqEntity;
import com.codezj.domain.trade.model.entity.SettlementRespEntity;
import com.codezj.domain.trade.service.settlement.TradeSettlementOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团业务完整流程集成测试
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupBuyIntegrationTest {

    @Resource
    private IMarketTradeService marketTradeService;

    @Resource
    private TradeSettlementOrderService tradeSettlementOrderService;

    /**
     * 测试完整拼团流程：锁单 -> 支付 -> 结算
     */
    @Test
    public void testCompleteGroupBuyFlow() throws Exception {
        log.info("=== 开始测试完整拼团流程 ===");

        // 1. 第一个用户发起拼团（团长）
        String teamLeaderUserId = "user_001";
        String teamId = createGroupBuyOrder(teamLeaderUserId, null, "团长发起拼团");

        // 2. 其他用户参与拼团
        List<String> participants = new ArrayList<>();
        participants.add("user_002");
        participants.add("user_003");

        for (String userId : participants) {
            createGroupBuyOrder(userId, teamId, "参与拼团");
        }

        // 3. 模拟支付完成，执行结算
        executeSettlement(teamLeaderUserId);
        for (String userId : participants) {
            executeSettlement(userId);
        }

        log.info("=== 完整拼团流程测试完成 ===");
    }

    /**
     * 测试高并发拼团场景
     */
    @Test
    public void testConcurrentGroupBuy() throws InterruptedException {
        log.info("=== 开始测试高并发拼团场景 ===");

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    String userIdStr = "concurrent_user_" + String.format("%03d", userId);
                    createGroupBuyOrder(userIdStr, null, "并发测试");
                    successCount.incrementAndGet();
                    log.info("用户 {} 拼团成功", userIdStr);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("用户 concurrent_user_{} 拼团失败: {}", String.format("%03d", userId), e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        log.info("=== 并发测试结果 ===");
        log.info("成功数量: {}", successCount.get());
        log.info("失败数量: {}", failCount.get());
        log.info("成功率: {}%", (successCount.get() * 100.0 / threadCount));
    }

    /**
     * 测试责任链规则过滤
     */
    @Test
    public void testRuleFilterChain() {
        log.info("=== 开始测试责任链规则过滤 ===");

        // 测试用户参与次数限制
        try {
            String userId = "limit_test_user";
            // 多次参与同一活动，测试限制规则
            for (int i = 0; i < 5; i++) {
                createGroupBuyOrder(userId, null, "测试参与次数限制 - 第" + (i+1) + "次");
            }
        } catch (Exception e) {
            log.info("规则过滤生效，用户参与次数受限: {}", e.getMessage());
        }

        log.info("=== 责任链规则过滤测试完成 ===");
    }

    private String createGroupBuyOrder(String userId, String teamId, String scenario) {
        try {
            LockMarketPayOrderRequestDTO request = new LockMarketPayOrderRequestDTO();
            request.setUserId(userId);
            request.setSource("s01");
            request.setChannel("c01");
            request.setGoodsId("9890001");
            request.setActivityId(10001L);
            request.setOutTradeNo(generateOutTradeNo(userId));
            request.setTeamId(teamId);

            Response<LockMarketPayOrderResponseDTO> response = marketTradeService.lockMarketPayOrder(request);

            if ("0000".equals(response.getCode())) {
                String resultTeamId = response.getData().getOrderId();
                log.info("{} - 用户 {} 锁单成功，订单ID: {}", scenario, userId, resultTeamId);
                return resultTeamId;
            } else {
                log.error("{} - 用户 {} 锁单失败: {}", scenario, userId, response.getInfo());
                throw new RuntimeException(response.getInfo());
            }
        } catch (Exception e) {
            log.error("{} - 用户 {} 锁单异常: {}", scenario, userId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void executeSettlement(String userId) {
        try {
            SettlementReqEntity settlementReq = SettlementReqEntity.builder()
                    .userId(userId)
                    .source("s01")
                    .channel("c01")
                    .outTradeNo(generateOutTradeNo(userId))
                    .build();

            SettlementRespEntity settlementResp = tradeSettlementOrderService.executeSettlement(settlementReq);
            log.info("用户 {} 结算成功，团ID: {}", userId, settlementResp.getTeamId());
        } catch (Exception e) {
            log.error("用户 {} 结算失败: {}", userId, e.getMessage());
        }
    }

    private String generateOutTradeNo(String userId) {
        return userId + "_" + System.currentTimeMillis();
    }
}

