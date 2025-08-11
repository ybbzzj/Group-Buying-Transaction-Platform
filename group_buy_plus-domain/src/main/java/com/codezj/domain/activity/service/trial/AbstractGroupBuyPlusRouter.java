package com.codezj.domain.activity.service.trial;

import com.codezj.domain.activity.adapter.repository.IActivityRepository;
import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.types.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.codezj.types.design.framework.tree.AbstractStrategyRouter;
import com.codezj.types.design.framework.tree.StrategyHandler;

import javax.annotation.Resource;

/**
 * 抽象拼单路由类
 *
 * @param <MarketProductEntity> 拼单商品实体
 * @param <GroupBuyContext>     拼单上下文
 * @param <TrialBalanceEntity>  试算结果
 */
public abstract class AbstractGroupBuyPlusRouter<MarketProductEntity, GroupBuyContext, TrialBalanceEntity>
        extends AbstractMultiThreadStrategyRouter<MarketProductEntity, GroupBuyContext, TrialBalanceEntity> {


    @Resource
    protected IActivityRepository repository;

    /**
     * 多线程异步加载数据，默认实现。有的业务节点用不到，可以不实现
     */
    @Override
    protected void multiThread(MarketProductEntity params, GroupBuyContext context) throws Exception {
        // 缺省的方法
    }
}
