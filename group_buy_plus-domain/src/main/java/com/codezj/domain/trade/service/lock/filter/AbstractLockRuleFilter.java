package com.codezj.domain.trade.service.lock.filter;

import com.codezj.domain.trade.adapter.repository.ITradeRepository;
import com.codezj.domain.trade.model.entity.LockRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterRespEntity;
import com.codezj.domain.trade.service.lock.factory.TraderLockRuleFilterFactory;
import com.codezj.types.design.framework.link.multition.handler.ILogicHandler;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 规则过滤器抽象类
 **/
public abstract class AbstractLockRuleFilter implements ILogicHandler<LockRuleFilterParamsEntity, TraderLockRuleFilterFactory.LockRuleFilterContext, LockRuleFilterRespEntity> {

    @Resource
    protected ITradeRepository repository;

    // 默认实现
    public LockRuleFilterRespEntity apply(LockRuleFilterParamsEntity requestParameter, TraderLockRuleFilterFactory.LockRuleFilterContext dynamicContext) throws Exception {
        // 缺省
        return null;
    }
}
