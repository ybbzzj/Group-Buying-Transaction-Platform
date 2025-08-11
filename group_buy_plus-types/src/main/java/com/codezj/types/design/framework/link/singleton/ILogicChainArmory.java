package com.codezj.types.design.framework.link.singleton;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 责任链装配接口
 **/
public interface ILogicChainArmory<T, D, R> {

    ILogicLink<T, D, R> next();

    ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next);
}
