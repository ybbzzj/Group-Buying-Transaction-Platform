package com.codezj.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.codezj.types.annotation.RCC;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.codezj.types.common.Constants.SPLIT;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: RCC Bean工厂类
 * 1. 扫描并初始化@RCC注解的Bean的字段
 * 2. 动态接收Redis的pub，更新字段值
 * 继承BeanPostProcessor接口，可以获取Spring管理的Bean
 **/
@Slf4j
@Configuration
public class RCCBeanFactory implements BeanPostProcessor {

    private final String RCC_KEY_PREFIX = "group_buy_plus:rcc:";

    // Spring 推荐方式：构造函数注入是 Spring 官方推荐的依赖注入方式
    private final RedissonClient redissonClient;

    /**
     * Spring 容器自动调用构造器。具体过程：
     * 1. Spring 容器启动时，扫描到 @Configuration 注解的 DCCValueBeanFactory 类
     * 2. 容器识别到这个类需要一个 RedissonClient 类型的依赖
     * 3. 容器在应用上下文中查找 RedissonClient 类型的 Bean
     * 4. 找到后，自动调用这个构造函数创建 DCCValueBeanFactory 实例
     * 5. 将创建的实例注册为 Spring Bean
     *
     * @param redissonClient Redisson 客户端
     */
    @Lazy
    public RCCBeanFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private final Map<String, Object> rccBeanMap = new HashMap<>();

    /**
     * 初始化@RCC注解的Bean的字段
     * 这个方法有默认实现
     *
     * @param bean     需要处理的Bean
     * @param beanName Bean的名称
     */
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        try {
            // 反射获取Bean的类
            Class<?> beanClazz = bean.getClass();
            // 获取Bean对象
            Object beanObject = bean;

            if (AopUtils.isAopProxy(beanObject)) {
                // 代理对象，需要拿到真实的类和对象
                beanClazz = AopUtils.getTargetClass(beanClazz);
                beanObject = AopProxyUtils.getSingletonTarget(beanObject);
            }

            // 遍历Bean的字段
            Field[] fields = beanClazz.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(RCC.class)) {
                    // 没有@RCC注解
                    return bean;
                }

                // 获取@RCC注解
                RCC annotation = field.getAnnotation(RCC.class);
                // 获取@RCC注解的value值
                String rccValue = annotation.value();
                if (StringUtils.isBlank(rccValue)) {
                    // @RCC注解的value值为空
                    throw new RuntimeException("[@RCC配置初始化] RCC配置为空");
                }
                String[] split = rccValue.split(":", 2);
                if (split.length != 2) {
                    // 格式错误
                    throw new RuntimeException("[@RCC配置初始化] RCC配置格式错误");
                }
                String fieldName = split[0];
                String value = split[1];
                if (StringUtils.isBlank(fieldName) || StringUtils.isBlank(value)) {
                    // 格式错误
                    throw new RuntimeException("[@RCC配置初始化] key或value为空");
                }

                String key = RCC_KEY_PREFIX + fieldName; // 拼接Redis的key

                Class<?> fieldType = field.getType();
                if (fieldType == String.class) {
                    // 处理String类型
                    handleStringRCC(beanObject, field, fieldName, value, key);
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    // 处理Map类型
                    handleMapRCC(beanObject, field, fieldName, value, key);
                } else {
                    // 暂不支持
                    log.error("[@RCC配置初始化] 暂不支持 {} 类型", fieldType.getName());
                    continue;
                }

                rccBeanMap.put(key, beanObject);
            }
        } catch (Exception e) {
            throw new RuntimeException("[@RCC配置初始化] Bean: " + beanName + "RCC初始化失败");
        }
        return bean;
    }

    private void handleMapRCC(Object beanObject, Field field, String fieldName, String value, String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            value = bucket.get();
        } else {
            bucket.set(value);
        }

        // 解析value转换为Map
        Map<?, ?> mapValue = JSON.parseObject(value, Map.class);
        try {
            // 设置访问权限
            field.setAccessible(true);
            field.set(beanObject, mapValue);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("[@RCC配置初始化] 未知错误");
        }
        log.info("[@RCC配置初始化] Map类型字段，field: {}, value: {}", fieldName, value);


    }

    private void handleStringRCC(Object beanObject, Field field, String fieldName, String value, String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            // Redis中有配置值，修改为配置值
            value = bucket.get();
        } else {
            // Redis中没有配置值，初始化配置值
            bucket.set(value);
        }

        try {
            // 设置访问权限
            field.setAccessible(true);
            field.set(beanObject, value);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("[@RCC配置初始化] 未知错误");
        }
        log.info("[@RCC配置初始化] String类型字段，field: {}, value: {}", fieldName, value);

    }

    @Bean("rccTopic")
    public RTopic rccRedisListener() {
        RTopic rccTopic = redissonClient.getTopic("group_market_plus_rcc");
        // 第一个参数 String.class：
        // 指定消息的类型
        // 表示从 Redis Topic 接收的消息将被反序列化为 String 类型
        // Redisson 会根据这个类型信息自动处理消息的反序列化
        // 第二个参数 (channel, msg) -> {...}：
        // 一个 Lambda 表达式，实现了 MessageListener<T> 接口
        // 包含两个参数：
        // channel：频道名称（Topic 名称）
        // msg：接收到的消息内容（已转换为 String 类型）
        // 当 Topic 收到消息时，这个 Lambda 表达式会被执行
        rccTopic.addListener(String.class, (channel, msg) -> {
            String[] split = msg.split(SPLIT, 2);
            String fieldName = split[0];
            String key = RCC_KEY_PREFIX + fieldName;
            String value = split[1];
            RBucket<String> bucket = redissonClient.getBucket(key);
            if (!bucket.isExists()) {
                return;
            }
            bucket.set(value);

            // 获取到@RCC注解的Bean
            Object beanObject = rccBeanMap.get(key);
            if (Objects.isNull(beanObject)) {
                return;
            }

            Class<?> clazz = beanObject.getClass();
            if (AopUtils.isAopProxy(beanObject)) {
                clazz = AopUtils.getTargetClass(clazz);
            }


            try {
                // 修改Bean的字段值
                Field field = clazz.getDeclaredField(fieldName);
                // 字段类型
                Class<?> fieldType = field.getType();

                field.setAccessible(true);

                if (fieldType == String.class) {
                    field.set(beanObject, value);
                    log.info("[@RCC配置更新] RCC配置更新成功，key:{}, newValue:{}", key, value);
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    Map<?, ?> mapValue = JSON.parseObject(value, Map.class);
                    field.set(beanObject, mapValue);
                    log.info("[@RCC配置更新] Map类型字段更新成功，key:{}, newValue:{}", key, value);
                }

                field.setAccessible(false);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("[@RCC配置更新] " + fieldName +"字段不存在");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("[@RCC配置更新] " + fieldName + "字段无法访问");
            } catch (Exception e) {
                throw new RuntimeException("[@RCC配置更新] 未知错误");
            }

        });
        return rccTopic;
    }
}
