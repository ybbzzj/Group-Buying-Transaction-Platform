package com.codezj.test.types;

import com.codezj.test.types.rule02.factory.Rule02TradeRuleFactory;
import com.codezj.test.types.rule02.logic.TResponse;
import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Link02Test {

    @Resource(name = "demo01")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, TResponse> businessLinkedList01;

    @Resource(name = "demo02")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, TResponse> businessLinkedList02;

    @Test
    public void test_model02_01() throws Exception {
        TResponse apply = businessLinkedList01.apply("123", new Rule02TradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(apply));
    }

    @Test
    public void test_model02_02() throws Exception {
        TResponse apply = businessLinkedList02.apply("123", new Rule02TradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(apply));
    }

}
