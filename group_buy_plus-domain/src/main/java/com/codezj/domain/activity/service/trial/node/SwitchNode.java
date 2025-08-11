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

/**
 * 降级、切流节点
 */
@Slf4j
@Service
public class SwitchNode extends AbstractGroupBuyPlusRouter<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> {

    @Resource
    private TagNode tagNode;

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) throws Exception {
        log.info("[拼团商品优惠试算服务] SwitchNode. process some business logic...");

        // 开关、切量、白名单
        if (repository.isDowngrade()) {
            log.info("[拼团商品优惠试算服务] 降级开启中。");
            throw new AppException(ResponseCode.E0003.getCode(), ResponseCode.E0003.getInfo());
        }
        // 不拦截未登录用户
        if (!StringUtils.isBlank(params.getUserId()) && repository.isInCutFlowRange(params.getUserId())) {
            log.info("[拼团商品优惠试算服务] 切量开启中。用户{}流量被拦截", params.getUserId());
            throw new AppException(ResponseCode.E0004.getCode(), ResponseCode.E0004.getInfo());
        }

        if (repository.isInWhiteList() && !repository.isInWhiteList(params.getUserId())) {
            log.info("[拼团商品优惠试算服务] 当前服务处于白名单模式。用户{}不在白名单中", params.getUserId());
            throw new AppException(ResponseCode.E0005.getCode(), ResponseCode.E0005.getInfo());
        }

        return route(params, context);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> get(MarketProductEntity params, DefaultActivityStrategyFactory.GroupBuyContext context) {
        return tagNode;
    }
}
