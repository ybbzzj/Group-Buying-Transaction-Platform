package com.codezj.test.types.rule02.logic;

import com.codezj.test.types.rule02.factory.Rule02TradeRuleFactory;
import com.codezj.types.design.framework.link.multition.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RuleLogic202 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, TResponse> {

    public TResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception{

        log.info("link model02 RuleLogic202");

        return new TResponse("hi RuleLogic202");
    }

}
