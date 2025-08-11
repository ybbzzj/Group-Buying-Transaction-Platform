package com.codezj.types.design.framework.tree;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 抽象策略路由
 * @param <T> 参数类型
 * @param <D> 上下文
 * @param <R> 返回值类型
 */
public abstract class AbstractStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R> {

    protected StrategyHandler<T, D, R> DEFAULT = StrategyHandler.DEFAULT_HANDLER;

    public R route(T params, D context) throws Exception  {
        StrategyHandler<T, D, R> handler = get(params, context);
        if (Objects.isNull(handler)) {
            return DEFAULT.handle(params, context);
        }
        return handler.handle(params, context);
    }
}
