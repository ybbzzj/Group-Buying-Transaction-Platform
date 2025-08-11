package com.codezj.test.types.rule02.factory;

import com.codezj.test.types.rule02.logic.RuleLogic201;
import com.codezj.test.types.rule02.logic.RuleLogic202;
import com.codezj.test.types.rule02.logic.TResponse;
import com.codezj.types.design.framework.link.multition.LinkArmory;
import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class Rule02TradeRuleFactory {

    @Bean("demo01")
    public BusinessLinkedList<String, DynamicContext, TResponse> demo01(RuleLogic201 ruleLogic201, RuleLogic202 ruleLogic202) {

        LinkArmory<String, DynamicContext, TResponse> linkArmory = new LinkArmory<>("demo01", ruleLogic201, ruleLogic202);

        return linkArmory.getLogicLink();
    }

    @Bean("demo02")
    public BusinessLinkedList<String, DynamicContext, TResponse> demo02(RuleLogic202 ruleLogic202) {

        LinkArmory<String, DynamicContext, TResponse> linkArmory = new LinkArmory<>("demo02", ruleLogic202);

        return linkArmory.getLogicLink();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {
        private String age;
    }

}
