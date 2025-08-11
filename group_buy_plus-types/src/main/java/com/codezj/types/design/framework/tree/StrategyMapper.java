package com.codezj.types.design.framework.tree;

/**
 * 策略映射器
 * @param <T> 参数类型
 * @param <D> 上下文
 * @param <R> 返回值类型
 */
public interface StrategyMapper<T, D, R> {

    StrategyHandler<T, D, R> get(T params, D context);
}
