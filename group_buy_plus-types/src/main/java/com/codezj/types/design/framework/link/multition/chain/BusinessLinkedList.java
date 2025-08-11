package com.codezj.types.design.framework.link.multition.chain;

import com.codezj.types.design.framework.link.multition.handler.ILogicHandler;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 业务链表
 **/
public class BusinessLinkedList<T, D, R> extends LinkedList<ILogicHandler<T, D, R>> implements ILogicHandler<T, D, R>{

    public BusinessLinkedList(String name) {
        super(name);
    }

    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        Node<ILogicHandler<T, D, R>> current = this.first;
        do {
            ILogicHandler<T, D, R> item = current.item;
            R apply = item.apply(requestParameter, dynamicContext);
            if (null != apply) return apply;

            current = current.next;
        } while (null != current);

        return null;
    }

}
