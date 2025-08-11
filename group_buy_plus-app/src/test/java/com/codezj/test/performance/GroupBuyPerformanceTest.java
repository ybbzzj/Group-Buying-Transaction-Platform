package com.codezj.test.performance;

import com.codezj.api.IMarketTradeService;
import com.codezj.api.dto.LockMarketPayOrderRequestDTO;
import com.codezj.api.dto.LockMarketPayOrderResponseDTO;
import com.codezj.api.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团系统性能压力测试
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupBuyPerformanceTest {

    @Resource
    private IMarketTradeService marketTradeService;

    /**
     * 高并发锁单性能测试
     * 测试指标：TPS、响应时间、成功率
     */
    @Test
    public void testHighConcurrencyLockOrder() throws InterruptedException {
        log.info("=== 开始高并发锁单性能测试 ===");

        int threadCount = 100;  // 并发线程数
        int requestPerThread = 10;  // 每个线程的请求数
        int totalRequests = threadCount * requestPerThread;

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestPerThread; j++) {
                        long requestStart = System.currentTimeMillis();

                        try {
                            String userId = "perf_user_" + threadId + "_" + j;
                            LockMarketPayOrderRequestDTO request = createLockOrderRequest(userId);

                            Response<LockMarketPayOrderResponseDTO> response = marketTradeService.lockMarketPayOrder(request);

                            long requestEnd = System.currentTimeMillis();
                            totalResponseTime.addAndGet(requestEnd - requestStart);

                            if ("0000".equals(response.getCode())) {
                                successCount.incrementAndGet();
                            } else {
                                failCount.incrementAndGet();
                                log.debug("锁单失败: {}", response.getInfo());
                            }
                        } catch (Exception e) {
                            failCount.incrementAndGet();
                            long requestEnd = System.currentTimeMillis();
                            totalResponseTime.addAndGet(requestEnd - requestStart);
                            log.debug("锁单异常: {}", e.getMessage());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 计算性能指标
        double tps = (double) totalRequests / (totalTime / 1000.0);
        double avgResponseTime = (double) totalResponseTime.get() / totalRequests;
        double successRate = (double) successCount.get() / totalRequests * 100;

        log.info("=== 性能测试结果 ===");
        log.info("总请求数: {}", totalRequests);
        log.info("并发线程数: {}", threadCount);
        log.info("成功数量: {}", successCount.get());
        log.info("失败数量: {}", failCount.get());
        log.info("总耗时: {} ms", totalTime);
        log.info("TPS: {} 请求/秒", tps);
        log.info("平均响应时间: {} ms", avgResponseTime);
        log.info("成功率: {}%", successRate);

        // 性能基准断言
        assert tps > 50 : "TPS应该大于50";
        assert avgResponseTime < 1000 : "平均响应时间应该小于1000ms";
        assert successRate > 80 : "成功率应该大于80%";
    }

    /**
     * 内存使用情况测试
     */
    @Test
    public void testMemoryUsage() throws InterruptedException {
        log.info("=== 开始内存使用情况测试 ===");

        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        log.info("初始内存使用: {} MB", initialMemory / 1024 / 1024);

        // 执行大量请求
        int requestCount = 1000;
        for (int i = 0; i < requestCount; i++) {
            try {
                String userId = "memory_test_user_" + i;
                LockMarketPayOrderRequestDTO request = createLockOrderRequest(userId);
                marketTradeService.lockMarketPayOrder(request);

                if (i % 100 == 0) {
                    long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                    log.info("执行{}次请求后内存使用: {} MB", i, currentMemory / 1024 / 1024);
                }
            } catch (Exception e) {
                // 忽略业务异常，专注内存测试
            }
        }

        // 强制垃圾回收
        System.gc();
        Thread.sleep(1000);

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        log.info("最终内存使用: {} MB", finalMemory / 1024 / 1024);
        log.info("内存增长: {} MB", memoryIncrease / 1024 / 1024);

        // 内存泄漏检测
        assert memoryIncrease < 100 * 1024 * 1024 : "内存增长不应超过100MB";
    }

    /**
     * 责任链性能测试
     */
    @Test
    public void testRuleChainPerformance() throws InterruptedException {
        log.info("=== 开始责任链性能测试 ===");

        int testCount = 10000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCount; i++) {
            try {
                String userId = "chain_test_user_" + i;
                LockMarketPayOrderRequestDTO request = createLockOrderRequest(userId);
                marketTradeService.lockMarketPayOrder(request);
            } catch (Exception e) {
                // 忽略业务异常，专注性能测试
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / testCount;

        log.info("责任链执行{}次，总耗时: {} ms", testCount, totalTime);
        log.info("平均每次执行时间: {:.2f} ms", avgTime);

        // 性能断言
        assert avgTime < 10 : "责任链平均执行时间应该小于10ms";
    }

    private LockMarketPayOrderRequestDTO createLockOrderRequest(String userId) {
        LockMarketPayOrderRequestDTO request = new LockMarketPayOrderRequestDTO();
        request.setUserId(userId);
        request.setSource("s01");
        request.setChannel("c01");
        request.setGoodsId("9890001");
        request.setActivityId(100123L);
        request.setTeamId("00468899");
        request.setOutTradeNo(RandomStringUtils.randomNumeric(12));
        return request;
    }
}

