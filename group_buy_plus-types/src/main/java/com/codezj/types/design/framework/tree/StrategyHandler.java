package com.codezj.types.design.framework.tree;

/**
 * 策略处理器
 * @param <T> 参数类型
 * @param <D> 上下文
 * @param <R> 返回值类型
 */
public interface StrategyHandler<T, D, R> {
    StrategyHandler DEFAULT_HANDLER = (T, D) -> null;
    R handle(T params, D context) throws Exception;
}
