package com.codezj.types.design.framework.link.singleton;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 责任链接口（继承责任链装配）
 * 责任链节点的处理逻辑和装配逻辑没有解耦，导致两个业务处理逻辑不能分别拥有自己的装配逻辑，
 * 其中一个业务处理逻辑的装配会影响另一个业务处理逻辑的装配
 **/
public interface ILogicLink<T, D, R> extends ILogicChainArmory<T, D, R> {

    R apply(T params, D context) throws Exception;
}
