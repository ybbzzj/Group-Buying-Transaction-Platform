package com.codezj.types.design.framework.tree;

import java.util.Objects;

/**
 * 策略路由抽象类
 * 支持多线程，支持在业务逻辑中异步加载数据到上下文
 * @param <T>
 * @param <D>
 * @param <R>
 */
public abstract class AbstractMultiThreadStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R> {

    protected StrategyHandler<T, D, R> DEFAULT = StrategyHandler.DEFAULT_HANDLER;

    /**
     * 业务逻辑流转
     */
    public R route(T params, D context) throws Exception {
        StrategyHandler<T, D, R> handler = get(params, context);
        if (Objects.isNull(handler)) {
            return DEFAULT.handle(params, context);
        }
        return handler.handle(params, context);
    }


    /**
     * 节点处理逻辑
     *
     * @param params  入参
     * @param context 上下文
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public R handle(T params, D context) throws Exception {
        multiThread(params, context);
        return doApply(params, context);
    }

    /**
     * 多线程异步加载数据。在节点处理过程中，因为需要加载数据到上下文，压缩后续节点处理耗时
     * 所以需要多线程异步加载数据。
     * 注意：多线程加载数据时，需要考虑线程安全。
     */
    protected abstract void multiThread(T params, D context) throws Exception;

    /**
     * 业务流程受理
     */
    protected abstract R doApply(T params, D context) throws Exception;

}
