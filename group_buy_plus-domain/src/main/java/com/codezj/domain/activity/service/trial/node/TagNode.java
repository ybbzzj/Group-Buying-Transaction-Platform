package com.codezj.domain.activity.service.trial.node;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.valobj.DiscountTypeEnum;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SkuVO;
import com.codezj.domain.activity.service.trial.AbstractGroupBuyPlusRouter;
import com.codezj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.codezj.domain.activity.service.trial.thread.QueryGroupBuyActivityDiscountVOThreadTask;
import com.codezj.domain.activity.service.trial.thread.QuerySkuVOFromDBThreadTask;
import com.codezj.types.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 人群标签过滤节点
 **/
@Slf4j
@Service
public class TagNode extends AbstractGroupBuyPlusRouter<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 构建上下文超时时间
     */
    private final int BUILD_CONTEXT_TIMEOUT = 10000;

    @Resource
    private MarketNode marketNode;

    @Resource
    private EndNode endNode;

    @Resource
    private ErrorNode errorNode;

    @Override
    protected void multiThread(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext groupBuyContext) throws Exception {
        // 异步查询活动折扣配置
        QueryGroupBuyActivityDiscountVOThreadTask queryGroupBuyActivityDiscountVOThreadTask = new QueryGroupBuyActivityDiscountVOThreadTask(params.getSource(), params.getChannel(), params.getGoodsId(), repository);
        FutureTask<GroupBuyActivityDiscountVO> groupBuyActivityDiscountVOFutureTask = new FutureTask<>(queryGroupBuyActivityDiscountVOThreadTask);
        threadPoolExecutor.execute(groupBuyActivityDiscountVOFutureTask);

        // 异步查询商品信息
        QuerySkuVOFromDBThreadTask querySkuVOFromDBThreadTask = new QuerySkuVOFromDBThreadTask(params.getGoodsId(), params.getSource(), params.getChannel(), repository);
        FutureTask<SkuVO> skuVOFutureTask = new FutureTask<>(querySkuVOFromDBThreadTask);
        threadPoolExecutor.execute(skuVOFutureTask);

        // 写入上下文 - 对于一些复杂场景，获取数据的操作，有时候会在下N个节点获取，这样前置查询数据，可以提高接口响应效率
        // 注意：FutureTask.get()方法会阻塞当前线程
        // 超时时间
        groupBuyContext.setActivityDiscount(groupBuyActivityDiscountVOFutureTask.get(BUILD_CONTEXT_TIMEOUT, TimeUnit.MILLISECONDS));
        groupBuyContext.setSku(skuVOFutureTask.get(BUILD_CONTEXT_TIMEOUT, TimeUnit.MILLISECONDS));

        log.info("[拼团商品优惠试算服务]TagNode. 异步加载数据完成, userId: {}", params.getUserId());

    }

    @Override
    protected TrialBalanceEntity doApply(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) throws Exception {
        log.info("[拼团商品优惠试算服务]TagNode. 开始筛选标签...");

        GroupBuyActivityDiscountVO activityDiscount = context.getActivityDiscount();
        SkuVO sku = context.getSku();
        if (Objects.isNull(activityDiscount) || Objects.isNull(sku) || Objects.isNull(activityDiscount.getGroupBuyDiscount())) {
            // 活动配置有误，路由到error节点
            log.info("[拼团商品优惠试算服务]TagNode. 活动配置有误, 路由到error节点");
            return route(params, context);
        }
        GroupBuyActivityDiscountVO.GroupBuyDiscount discount = activityDiscount.getGroupBuyDiscount();
        if (DiscountTypeEnum.BASE.getCode().equals(discount.getDiscountType())) {
            // 基础优惠，所有人可见可参与
            // Discount is all you need !
            log.info("[拼团商品优惠试算服务]TagNode. 基础优惠, 所有人可见可参与");
            context.setIsVisible(true);
            context.setIsEnable(true);
            return route(params, context);
        }
        // 标签优惠
        if (StringUtils.isBlank(discount.getTagId())) {
            // 没有标签的默认所有人可见可参与
            log.info("[拼团商品优惠试算服务]TagNode. 没有标签的活动所有人可见可参与");
            context.setIsVisible(true);
            context.setIsEnable(true);
            return route(params, context);
        }
        // 判断用户是否命中标签
        Boolean isTagCrowd = repository.isTagCrowdRange(params.getUserId(), discount.getTagId());

        if (isTagCrowd) {
            // 用户命中标签，可见可参与
            log.info("[拼团商品优惠试算服务]TagNode. 用户{}命中标签, 可见可参与", params.getUserId());
            context.setIsVisible(true);
            context.setIsEnable(true);
        } else {
            // 用户未命中标签
            boolean visible = activityDiscount.isVisible();
            boolean enable = activityDiscount.isEnable();
            context.setIsVisible(visible);
            // todo 暂时理解可参与的前提是可见，若不可见则不可参与
            context.setIsEnable(visible && enable);
            log.info("[拼团商品优惠试算服务]TagNode. 用户{}未命中标签, 可见:{}, 可参与:{}", params.getUserId(), visible, enable);
        }
        return route(params, context);

    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> get(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        if (Objects.isNull(context.getActivityDiscount()) ||
            Objects.isNull(context.getSku()) ||
            Objects.isNull(context.getActivityDiscount().getGroupBuyDiscount())) {
            // 活动配置有误，路由到error
            return errorNode;
        }
        if (!context.getIsVisible() && !context.getIsEnable()) {
            // 不可见且不可参与，路由到end
            return endNode;
        }
        // 享受优惠，需要进行优惠计算，路由到market
        return marketNode;
    }
}
