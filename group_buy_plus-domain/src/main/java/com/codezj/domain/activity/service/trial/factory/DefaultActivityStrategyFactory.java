package com.codezj.domain.activity.service.trial.factory;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SkuVO;
import com.codezj.domain.activity.service.trial.node.RootNode;
import com.codezj.types.design.framework.tree.StrategyHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 默认拼单活动策略工厂
 */
@Service
public class DefaultActivityStrategyFactory {

    @Resource
    private RootNode rootNode;

    public DefaultActivityStrategyFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public StrategyHandler<MarketProductEntity, GroupBuyContext, TrialBalanceEntity> strategyHandler() {
        return rootNode;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupBuyContext {
        // 拼团活动营销配置值对象
        private GroupBuyActivityDiscountVO activityDiscount;
        // 商品信息
        private SkuVO sku;
        // 折扣价格
        private BigDecimal deductionPrice;
        // 支付价格
        private BigDecimal payPrice;
        // 是否可见
        private Boolean isVisible;
        // 是否可参与
        private Boolean isEnable;
    }
}
