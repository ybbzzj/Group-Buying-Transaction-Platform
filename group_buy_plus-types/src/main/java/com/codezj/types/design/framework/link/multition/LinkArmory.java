package com.codezj.types.design.framework.link.multition;

import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import com.codezj.types.design.framework.link.multition.handler.ILogicHandler;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 链路装配
 * 链路的装配和链路的处理逻辑解耦，每次装配链路会生成一个新的链路对象
 **/
public class LinkArmory<T, D, R> {

    private final BusinessLinkedList<T, D, R> logicLink;

    @SafeVarargs
    public LinkArmory(String linkName, ILogicHandler<T, D, R>... logicHandlers) {
        logicLink = new BusinessLinkedList<>(linkName);
        for (ILogicHandler<T, D, R> logicHandler: logicHandlers){
            logicLink.add(logicHandler);
        }
    }

    public BusinessLinkedList<T, D, R> getLogicLink() {
        return logicLink;
    }

}
