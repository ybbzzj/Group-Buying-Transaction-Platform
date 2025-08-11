package com.codezj.test.comprehensive;

import com.alibaba.fastjson.JSON;
import com.codezj.api.IMarketTradeService;
import com.codezj.api.dto.LockMarketPayOrderRequestDTO;
import com.codezj.api.dto.LockMarketPayOrderResponseDTO;
import com.codezj.api.response.Response;
import com.codezj.domain.trade.model.entity.SettlementReqEntity;
import com.codezj.domain.trade.model.entity.SettlementRespEntity;
import com.codezj.domain.trade.service.settlement.TradeSettlementOrderService;
import com.codezj.infrastructure.dao.*;
import com.codezj.infrastructure.dao.po.CrowdTags;
import com.codezj.infrastructure.dao.po.CrowdTagsDetail;
import com.codezj.infrastructure.dao.po.GroupBuyActivity;
import com.codezj.infrastructure.dao.po.SCSkuActivity;
import com.codezj.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBitSet;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 综合业务测试 - 包含数据库、Redis、完整业务流程
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ComprehensiveBusinessTest {

    @Resource
    private IMarketTradeService marketTradeService;

    @Resource
    private TradeSettlementOrderService tradeSettlementOrderService;

    // @Resource
    // private IActivityService activityService;

    // @Resource
    // private ITagService tagService;

    @Resource
    private IRedisService redisService;

    // DAO层资源
    @Resource
    private IGroupBuyActivityDao groupBuyActivityDao;

    @Resource
    private IGroupBuyOrderDao groupBuyOrderDao;

    @Resource
    private ISCSkuActivityDao scSkuActivityDao;

    @Resource
    private ICrowdTagsDao crowdTagsDao;

    @Resource
    private ICrowdTagsDetailDao crowdTagsDetailDao;

    // @Resource
    // private IMarketPayOrderDao marketPayOrderDao;

    private Random random = new Random();

    /**
     * 测试前准备数据
     */
    @Before
    public void setUp() {
        log.info("=== 开始准备测试数据 ===");
        try {
            // 准备活动数据
            prepareActivityData();
            // 准备商品数据
            prepareSkuData();
            // 准备人群标签数据
            prepareCrowdTagsData();
            log.info("=== 测试数据准备完成 ===");
        } catch (Exception e) {
            log.warn("测试数据准备失败，可能数据库未连接: {}", e.getMessage());
        }
    }

    /**
     * 综合业务流程测试
     */
    @Test
    public void testCompleteBusinessFlow() {
        log.info("=== 开始综合业务流程测试 ===");

        try {
            // 1. 测试人群标签功能
            testCrowdTagsFeature();

            // 2. 测试活动查询功能
            testActivityQuery();

            // 3. 测试完整拼团流程
            testGroupBuyFlow();

            // 4. 测试Redis缓存功能
            testRedisCache();

            log.info("=== 综合业务流程测试完成 ===");
        } catch (Exception e) {
            log.error("综合业务流程测试失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 高并发拼团测试
     */
    @Test
    public void testConcurrentGroupBuy() {
        log.info("=== 开始高并发拼团测试 ===");

        int threadCount = 20;
        int requestPerThread = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestPerThread; j++) {
                        try {
                            String userId = "concurrent_user_" + threadId + "_" + j;
                            boolean success = performGroupBuyOrder(userId);
                            if (success) {
                                successCount.incrementAndGet();
                            } else {
                                failCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            failCount.incrementAndGet();
                            log.debug("并发测试异常: {}", e.getMessage());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            executor.shutdown();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            int totalRequests = threadCount * requestPerThread;
            double tps = (double) totalRequests / (totalTime / 1000.0);

            log.info("=== 高并发测试结果 ===");
            log.info("总请求数: {}", totalRequests);
            log.info("成功数: {}", successCount.get());
            log.info("失败数: {}", failCount.get());
            log.info("总耗时: {} ms", totalTime);
            log.info("TPS: {:.2f} 请求/秒", tps);
            log.info("成功率: {:.2f}%", (double) successCount.get() / totalRequests * 100);

        } catch (InterruptedException e) {
            log.error("并发测试被中断: {}", e.getMessage());
        }
    }

    /**
     * 数据库性能测试
     */
    @Test
    public void testDatabasePerformance() {
        log.info("=== 开始数据库性能测试 ===");

        try {
            // 批量插入测试
            testBatchInsert();

            // 查询性能测试
            testQueryPerformance();

            // 更新性能测试
            testUpdatePerformance();

        } catch (Exception e) {
            log.error("数据库性能测试失败: {}", e.getMessage());
        }
    }

    /**
     * Redis缓存性能测试
     */
    @Test
    public void testRedisPerformance() {
        log.info("=== 开始Redis缓存性能测试 ===");

        try {
            // 基本读写测试
            testBasicRedisOperations();

            // BitSet操作测试
            testBitSetOperations();

            // 缓存命中率测试
            testCacheHitRate();

        } catch (Exception e) {
            log.error("Redis缓存测试失败: {}", e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    private void prepareActivityData() {
        try {
            // 创建测试活动
            GroupBuyActivity activity = GroupBuyActivity.builder()
                    .activityId(10001L)
                    .activityName("大促销")
                    .discountId(25120208L)
                    .groupType(0)
                    .startTime(LocalDateTime.now().minusDays(1))
                    .endTime(LocalDateTime.now().plusDays(30))
                    .target(4)
                    .takeLimitCount(2)
                    .status(1)
                    .validTime("15")
                    .tagId("RQ_KJHKL98UU78H66554GFDV")
                    .tagScope("1,2")
                    .build();

            // 尝试插入，如果已存在则忽略
            try {
                groupBuyActivityDao.insert(activity);
                log.info("创建测试活动成功: {}", activity.getActivityId());
            } catch (Exception e) {
                log.debug("活动可能已存在: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("准备活动数据失败: {}", e.getMessage());
        }
    }

    private void prepareSkuData() {
        try {
            // 创建测试商品活动
            SCSkuActivity skuActivity = SCSkuActivity.builder()
                    .goodsId("9890001")
                    .activityId(10001L)
                    .source("s01")
                    .channel("c01")
                    .build();

            try {
                scSkuActivityDao.insert(skuActivity);
                log.info("创建测试商品活动成功: {}", skuActivity.getGoodsId());
            } catch (Exception e) {
                log.debug("商品活动可能已存在: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("准备商品数据失败: {}", e.getMessage());
        }
    }

    private void prepareCrowdTagsData() {
        try {
            // 创建人群标签
            CrowdTags crowdTags = CrowdTags.builder()
                    .tagId("TEST_TAG_001")
                    .tagName("测试人群标签")
                    .tagDesc("用于测试的人群标签")
                    .statistics(0)
                    .build();

            try {
                crowdTagsDao.insert(crowdTags);
                log.info("创建测试人群标签成功: {}", crowdTags.getTagId());
            } catch (Exception e) {
                log.debug("人群标签可能已存在: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("准备人群标签数据失败: {}", e.getMessage());
        }
    }

    private void testCrowdTagsFeature() {
        try {
            log.info("--- 测试人群标签功能 ---");

            // 添加用户到人群标签
            String[] testUsers = {"test_user_001", "test_user_002", "test_user_003"};
            for (String userId : testUsers) {
                try {
                    CrowdTagsDetail detail = CrowdTagsDetail.builder()
                            .tagId("TEST_TAG_001")
                            .userId(userId)
                            .build();
                    crowdTagsDetailDao.addCrowdTagsUserId(detail);
                    log.info("添加用户到标签成功: {}", userId);
                } catch (Exception e) {
                    log.debug("用户可能已在标签中: {}", e.getMessage());
                }
            }

            // 更新标签统计
            CrowdTags updateTags = CrowdTags.builder()
                    .tagId("TEST_TAG_001")
                    .statistics(testUsers.length)
                    .build();
            crowdTagsDao.updateCrowdTagsStatistics(updateTags);

            log.info("人群标签功能测试完成");
        } catch (Exception e) {
            log.warn("人群标签功能测试失败: {}", e.getMessage());
        }
    }

    private void testActivityQuery() {
        try {
            log.info("--- 测试活动查询功能 ---");

            List<GroupBuyActivity> activities = groupBuyActivityDao.queryGroupBuyActivityList();
            log.info("查询到活动数量: {}", activities.size());

            if (!activities.isEmpty()) {
                GroupBuyActivity activity = activities.get(0);
                log.info("活动详情: ID={}, 名称={}, 目标人数={}",
                    activity.getActivityId(), activity.getActivityName(), activity.getTarget());
            }

            log.info("活动查询功能测试完成");
        } catch (Exception e) {
            log.warn("活动查询功能测试失败: {}", e.getMessage());
        }
    }

    private void testGroupBuyFlow() {
        try {
            log.info("--- 测试完整拼团流程 ---");

            // 模拟用户拼团
            String userId = "flow_test_user_" + System.currentTimeMillis();
            boolean success = performGroupBuyOrder(userId);

            if (success) {
                log.info("拼团流程测试成功");

                // 测试结算流程
                try {
                    SettlementReqEntity settlementReq = SettlementReqEntity.builder()
                            .userId(userId)
                            .source("s01")
                            .channel("c01")
                            .outTradeNo(userId + "_" + System.currentTimeMillis())
                            .build();

                    SettlementRespEntity settlementResp = tradeSettlementOrderService.executeSettlement(settlementReq);
                    log.info("结算测试成功: {}", JSON.toJSONString(settlementResp));
                } catch (Exception e) {
                    log.warn("结算测试失败: {}", e.getMessage());
                }
            } else {
                log.warn("拼团流程测试失败");
            }

        } catch (Exception e) {
            log.warn("拼团流程测试失败: {}", e.getMessage());
        }
    }

    private void testRedisCache() {
        try {
            log.info("--- 测试Redis缓存功能 ---");

            // 测试基本操作
            String testKey = "test_key_" + System.currentTimeMillis();
            String testValue = "test_value_" + random.nextInt(1000);

            // 存储数据
            redisService.setValue(testKey, testValue);
            log.info("Redis存储成功: {} = {}", testKey, testValue);

            // 读取数据
            String retrievedValue = redisService.getValue(testKey);
            log.info("Redis读取成功: {} = {}", testKey, retrievedValue);

            // 验证数据一致性
            if (testValue.equals(retrievedValue)) {
                log.info("Redis数据一致性验证成功");
            } else {
                log.warn("Redis数据一致性验证失败");
            }

            // 测试BitSet操作
            testBitSetOperations();

        } catch (Exception e) {
            log.warn("Redis缓存测试失败: {}", e.getMessage());
        }
    }

    private boolean performGroupBuyOrder(String userId) {
        try {
            LockMarketPayOrderRequestDTO request = new LockMarketPayOrderRequestDTO();
            request.setUserId(userId);
            request.setSource("s01");
            request.setChannel("c01");
            request.setGoodsId("9890001");
            request.setActivityId(10001L);
            request.setOutTradeNo(userId + "_" + System.currentTimeMillis());

            Response<LockMarketPayOrderResponseDTO> response = marketTradeService.lockMarketPayOrder(request);

            if ("0000".equals(response.getCode())) {
                log.debug("用户 {} 拼团成功，订单ID: {}", userId, response.getData().getOrderId());
                return true;
            } else {
                log.debug("用户 {} 拼团失败: {}", userId, response.getInfo());
                return false;
            }
        } catch (Exception e) {
            log.debug("用户 {} 拼团异常: {}", userId, e.getMessage());
            return false;
        }
    }

    private void testBatchInsert() {
        try {
            log.info("--- 测试批量插入性能 ---");

            long startTime = System.currentTimeMillis();
            int batchSize = 100;

            for (int i = 0; i < batchSize; i++) {
                try {
                    CrowdTagsDetail detail = CrowdTagsDetail.builder()
                            .tagId("TEST_TAG_001")
                            .userId("batch_user_" + i + "_" + System.currentTimeMillis())
                            .build();
                    crowdTagsDetailDao.addCrowdTagsUserId(detail);
                } catch (Exception e) {
                    // 忽略重复插入错误
                }
            }

            long endTime = System.currentTimeMillis();
            log.info("批量插入{}条记录，耗时: {} ms", batchSize, endTime - startTime);

        } catch (Exception e) {
            log.warn("批量插入测试失败: {}", e.getMessage());
        }
    }

    private void testQueryPerformance() {
        try {
            log.info("--- 测试查询性能 ---");

            long startTime = System.currentTimeMillis();
            int queryCount = 50;

            for (int i = 0; i < queryCount; i++) {
                groupBuyActivityDao.queryGroupBuyActivityList();
            }

            long endTime = System.currentTimeMillis();
            double avgTime = (double) (endTime - startTime) / queryCount;
            log.info("执行{}次查询，平均耗时: {:.2f} ms", queryCount, avgTime);

        } catch (Exception e) {
            log.warn("查询性能测试失败: {}", e.getMessage());
        }
    }

    private void testUpdatePerformance() {
        try {
            log.info("--- 测试更新性能 ---");

            long startTime = System.currentTimeMillis();
            int updateCount = 20;

            for (int i = 0; i < updateCount; i++) {
                CrowdTags updateTags = CrowdTags.builder()
                        .tagId("TEST_TAG_001")
                        .statistics(random.nextInt(1000))
                        .build();
                crowdTagsDao.updateCrowdTagsStatistics(updateTags);
            }

            long endTime = System.currentTimeMillis();
            double avgTime = (double) (endTime - startTime) / updateCount;
            log.info("执行{}次更新，平均耗时: {:.2f} ms", updateCount, avgTime);

        } catch (Exception e) {
            log.warn("更新性能测试失败: {}", e.getMessage());
        }
    }

    private void testBasicRedisOperations() {
        try {
            log.info("--- 测试Redis基本操作性能 ---");

            long startTime = System.currentTimeMillis();
            int operationCount = 100;

            // 写入测试
            for (int i = 0; i < operationCount; i++) {
                redisService.setValue("perf_test_" + i, "value_" + i);
            }

            // 读取测试
            for (int i = 0; i < operationCount; i++) {
                redisService.getValue("perf_test_" + i);
            }

            long endTime = System.currentTimeMillis();
            double avgTime = (double) (endTime - startTime) / (operationCount * 2);
            log.info("执行{}次Redis读写操作，平均耗时: {:.2f} ms", operationCount * 2, avgTime);

        } catch (Exception e) {
            log.warn("Redis基本操作测试失败: {}", e.getMessage());
        }
    }

    private void testBitSetOperations() {
        try {
            log.info("--- 测试BitSet操作 ---");

            String bitSetKey = "test_bitset_" + System.currentTimeMillis();
            RBitSet bitSet = redisService.getBitSet(bitSetKey);

            // 设置一些位
            for (int i = 0; i < 100; i += 10) {
                bitSet.set(i, true);
            }

            // 检查位设置
            int setCount = 0;
            for (int i = 0; i < 100; i++) {
                if (bitSet.get(i)) {
                    setCount++;
                }
            }

            log.info("BitSet操作测试完成，设置位数: {}", setCount);

        } catch (Exception e) {
            log.warn("BitSet操作测试失败: {}", e.getMessage());
        }
    }

    private void testCacheHitRate() {
        try {
            log.info("--- 测试缓存命中率 ---");

            String cacheKey = "cache_test_" + System.currentTimeMillis();
            String cacheValue = "cached_value";

            // 首次访问（缓存未命中）
            long startTime = System.currentTimeMillis();
            String value1 = redisService.getValue(cacheKey);
            long firstAccessTime = System.currentTimeMillis() - startTime;

            // 设置缓存
            redisService.setValue(cacheKey, cacheValue);

            // 再次访问（缓存命中）
            startTime = System.currentTimeMillis();
            String value2 = redisService.getValue(cacheKey);
            long secondAccessTime = System.currentTimeMillis() - startTime;

            log.info("缓存未命中耗时: {} ms, 缓存命中耗时: {} ms", firstAccessTime, secondAccessTime);
            log.info("缓存性能提升: {:.2f}倍", (double) firstAccessTime / secondAccessTime);

        } catch (Exception e) {
            log.warn("缓存命中率测试失败: {}", e.getMessage());
        }
    }
}

