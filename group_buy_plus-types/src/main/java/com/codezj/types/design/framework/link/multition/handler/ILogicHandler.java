package com.codezj.types.design.framework.link.multition.handler;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 责任链处理逻辑
 **/
public interface ILogicHandler<T, D, R> {

    default R next(T requestParameter, D dynamicContext) {
        return null;
    }

    R apply(T requestParameter, D dynamicContext) throws Exception;

}
