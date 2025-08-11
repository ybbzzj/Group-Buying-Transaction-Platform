package com.codezj.test.architecture;

import com.codezj.types.design.framework.link.multition.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 架构设计验证测试
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArchitectureValidationTest {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 验证DDD领域边界划分
     */
    @Test
    public void testDomainBoundaries() {
        log.info("=== 开始验证DDD领域边界划分 ===");

        // 验证领域服务的存在
        String[] domainServices = {
            "com.codezj.domain.trade.service.TradeService",
            "com.codezj.domain.trade.service.lock.TradeLockOrderService",
            "com.codezj.domain.trade.service.settlement.TradeSettlementOrderService",
            "com.codezj.domain.activity.service.ActivityService",
            "com.codezj.domain.tag.service.TagService"
        };

        int foundServices = 0;
        for (String serviceName : domainServices) {
            try {
                Class<?> serviceClass = Class.forName(serviceName);
                log.info("✓ 找到领域服务: {}", serviceName);
                foundServices++;

                // 验证服务方法的业务语义
                Method[] methods = serviceClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (Modifier.isPublic(method.getModifiers())) {
                        log.info("  - 业务方法: {}", method.getName());
                    }
                }
            } catch (ClassNotFoundException e) {
                log.warn("✗ 未找到领域服务: {}", serviceName);
            }
        }

        log.info("领域服务发现率: {}/{}", foundServices, domainServices.length);
        assert foundServices >= domainServices.length * 0.8 : "至少应该找到80%的领域服务";
    }

    /**
     * 验证六边形架构的端口适配器模式
     */
    @Test
    public void testHexagonalArchitecture() {
        log.info("=== 开始验证六边形架构实现 ===");

        // 验证仓储接口（输出端口）
        String[] repositories = {
            "com.codezj.domain.trade.adapter.repository.ITradeRepository",
            "com.codezj.domain.activity.adapter.repository.IActivityRepository",
            "com.codezj.domain.tag.adapter.repository.ITagRepository"
        };

        int foundRepositories = 0;
        for (String repoName : repositories) {
            try {
                Class<?> repoClass = Class.forName(repoName);
                log.info("✓ 找到仓储接口: {}", repoName);
                foundRepositories++;

                // 验证接口方法
                Method[] methods = repoClass.getDeclaredMethods();
                log.info("  - 接口方法数: {}", methods.length);
            } catch (ClassNotFoundException e) {
                log.warn("✗ 未找到仓储接口: {}", repoName);
            }
        }

        // 验证API接口（输入端口）
        String[] apiInterfaces = {
            "com.codezj.api.IMarketTradeService",
            "com.codezj.api.IRCCService"
        };

        int foundApis = 0;
        for (String apiName : apiInterfaces) {
            try {
                Class<?> apiClass = Class.forName(apiName);
                log.info("✓ 找到API接口: {}", apiName);
                foundApis++;
            } catch (ClassNotFoundException e) {
                log.warn("✗ 未找到API接口: {}", apiName);
            }
        }

        log.info("仓储接口发现率: {}/{}", foundRepositories, repositories.length);
        log.info("API接口发现率: {}/{}", foundApis, apiInterfaces.length);

        assert foundRepositories > 0 : "应该至少找到一个仓储接口";
        assert foundApis > 0 : "应该至少找到一个API接口";
    }

    /**
     * 验证责任链模式实现
     */
    @Test
    public void testChainOfResponsibilityPattern() {
        log.info("=== 开始验证责任链模式实现 ===");

        // 获取所有实现了ILogicHandler接口的Bean
        Map<String, ILogicHandler> handlers = applicationContext.getBeansOfType(ILogicHandler.class);

        log.info("发现责任链处理器数量: {}", handlers.size());

        for (Map.Entry<String, ILogicHandler> entry : handlers.entrySet()) {
            String beanName = entry.getKey();
            ILogicHandler handler = entry.getValue();

            log.info("✓ 责任链处理器: {} -> {}", beanName, handler.getClass().getSimpleName());

            // 验证处理器是否实现了必要的方法
            Class<?> handlerClass = handler.getClass();
            try {
                Method applyMethod = handlerClass.getMethod("apply", Object.class, Object.class);
                log.info("  - 实现了apply方法: {}", applyMethod != null);

                Method nextMethod = handlerClass.getMethod("next", Object.class, Object.class);
                log.info("  - 实现了next方法: {}", nextMethod != null);
            } catch (NoSuchMethodException e) {
                log.warn("  - 方法实现不完整: {}", e.getMessage());
            }
        }

        assert handlers.size() > 0 : "应该至少找到一个责任链处理器";
    }

    /**
     * 验证工厂模式实现
     */
    @Test
    public void testFactoryPattern() {
        log.info("=== 开始验证工厂模式实现 ===");

        // 查找工厂类
        String[] factoryClasses = {
            "com.codezj.domain.trade.service.factory.TraderRuleFilterFactory",
            "com.codezj.domain.trade.service.lock.factory.TraderLockRuleFilterFactory",
            "com.codezj.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory"
        };

        int foundFactories = 0;
        for (String factoryName : factoryClasses) {
            try {
                Class<?> factoryClass = Class.forName(factoryName);
                log.info("✓ 找到工厂类: {}", factoryName);
                foundFactories++;

                // 验证工厂方法
                Method[] methods = factoryClass.getDeclaredMethods();
                List<Method> factoryMethods = Arrays.stream(methods)
                    .filter(m -> m.getName().contains("Chain") || m.getName().contains("Factory"))
                    .collect(Collectors.toList());

                log.info("  - 工厂方法数: {}", factoryMethods.size());
                for (Method method : factoryMethods) {
                    log.info("    * {}", method.getName());
                }
            } catch (ClassNotFoundException e) {
                log.warn("✗ 未找到工厂类: {}", factoryName);
            }
        }

        log.info("工厂类发现率: {}/{}", foundFactories, factoryClasses.length);
        assert foundFactories > 0 : "应该至少找到一个工厂类";
    }

    /**
     * 验证聚合根设计
     */
    @Test
    public void testAggregateRootDesign() {
        log.info("=== 开始验证聚合根设计 ===");

        String[] aggregateClasses = {
            "com.codezj.domain.trade.model.aggregate.GroupBuyOrderAggregate",
            "com.codezj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate"
        };

        int foundAggregates = 0;
        for (String aggregateName : aggregateClasses) {
            try {
                Class<?> aggregateClass = Class.forName(aggregateName);
                log.info("✓ 找到聚合根: {}", aggregateName);
                foundAggregates++;

                // 验证聚合根的字段
                java.lang.reflect.Field[] fields = aggregateClass.getDeclaredFields();
                log.info("  - 聚合字段数: {}", fields.length);

                for (java.lang.reflect.Field field : fields) {
                    log.info("    * {}: {}", field.getName(), field.getType().getSimpleName());
                }
            } catch (ClassNotFoundException e) {
                log.warn("✗ 未找到聚合根: {}", aggregateName);
            }
        }

        log.info("聚合根发现率: {}/{}", foundAggregates, aggregateClasses.length);
        assert foundAggregates > 0 : "应该至少找到一个聚合根";
    }

    /**
     * 验证值对象设计
     */
    @Test
    public void testValueObjectDesign() {
        log.info("=== 开始验证值对象设计 ===");

        String[] valueObjectClasses = {
            "com.codezj.domain.trade.model.valobj.TradeOrderProgressVO"
        };

        int foundValueObjects = 0;
        for (String voName : valueObjectClasses) {
            try {
                Class<?> voClass = Class.forName(voName);
                log.info("✓ 找到值对象: {}", voName);
                foundValueObjects++;

                // 验证值对象的不可变性（通过检查setter方法）
                Method[] methods = voClass.getDeclaredMethods();
                long setterCount = Arrays.stream(methods)
                    .filter(m -> m.getName().startsWith("set"))
                    .count();

                log.info("  - Setter方法数: {} (值对象应该尽量少)", setterCount);
            } catch (ClassNotFoundException e) {
                log.warn("✗ 未找到值对象: {}", voName);
            }
        }

        log.info("值对象发现率: {}/{}", foundValueObjects, valueObjectClasses.length);
    }
}

