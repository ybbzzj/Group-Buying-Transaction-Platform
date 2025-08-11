package com.codezj.domain.activity.service.trial.node;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.service.trial.AbstractGroupBuyPlusRouter;
import com.codezj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.codezj.types.design.framework.tree.StrategyHandler;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 错误节点
 **/
@Slf4j
@Service
public class ErrorNode extends AbstractGroupBuyPlusRouter<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> {

    @Override
    protected TrialBalanceEntity doApply(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) throws Exception {
        log.info("[拼团商品优惠试算服务]ErrorNode. 开始处理...");
        if (Objects.isNull(context.getActivityDiscount()) || Objects.isNull(context.getSku())) {
            log.error("[拼团商品优惠试算服务]ErrorNode. 无营销活动配置数据, MarketProductEntity: {}", params);
            throw new AppException(ResponseCode.E0002.getCode(), ResponseCode.E0002.getInfo());
        }
        return null;
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> get(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        return null;
    }
}
