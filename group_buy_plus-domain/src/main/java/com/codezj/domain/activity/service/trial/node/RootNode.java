package com.codezj.domain.activity.service.trial.node;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.service.trial.AbstractGroupBuyPlusRouter;
import com.codezj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.codezj.types.design.framework.tree.StrategyHandler;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 拼单开始节点
 */
@Slf4j
@Service
public class RootNode extends AbstractGroupBuyPlusRouter<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> {

    @Resource
    private SwitchNode switchNode;

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) throws Exception {
        if (Objects.isNull(params)) {
            return null;
        }
        log.info("[拼团商品优惠试算服务]RootNode. 开始处理请求，UserId: {}, params: {}", params.getUserId(), params);
        if (Objects.isNull(params.getGoodsId())
            || StringUtils.isBlank(params.getChannel())
            || StringUtils.isBlank(params.getSource())) {
            log.warn("[拼团商品优惠试算服务]RootNode. 入参不合法，无法计算优惠，params: {}", params);
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        return route(params, context);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> get(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        return switchNode;
    }
}
