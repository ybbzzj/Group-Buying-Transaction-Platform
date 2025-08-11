package com.codezj.types.design.framework.link.multition.chain;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 链接口
 **/
public interface ILink<E> {

    boolean add(E e);

    boolean addFirst(E e);

    boolean addLast(E e);

    boolean remove(Object o);

    E get(int index);

    void printLinkList();

}
