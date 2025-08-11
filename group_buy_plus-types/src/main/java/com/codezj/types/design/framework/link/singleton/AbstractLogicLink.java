package com.codezj.types.design.framework.link.singleton;

/**
 * @Author baozhongjie
 * @Version: v1.0.0
 * @Description: 抽象类，定义模版方法
 */
public abstract class AbstractLogicLink<T, D, R> implements ILogicLink<T, D, R> {

    private ILogicLink<T, D, R> next;

    @Override
    public ILogicLink<T, D, R> next() {
        return next;
    }

    @Override
    public ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next) {
        this.next = next;
        return next;
    }

    protected R next(T requestParameter, D dynamicContext) throws Exception {
        return next.apply(requestParameter, dynamicContext);
    }

}
