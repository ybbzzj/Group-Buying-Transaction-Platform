package com.codezj.domain.trade.service.lock.factory;

import com.codezj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterRespEntity;
import com.codezj.domain.trade.service.lock.filter.impl.ActivityEffectiveRuleFilter;
import com.codezj.domain.trade.service.lock.filter.impl.UserTakeLimitRuleFilter;
import com.codezj.types.design.framework.link.multition.LinkArmory;
import com.codezj.types.design.framework.link.multition.chain.BusinessLinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易规则过滤工厂类(责任链模式)
 **/
@Slf4j
@Service
public class TraderLockRuleFilterFactory {

    private final static String RULE_FILTER_CHAIN_NAME = "交易规则责任链";

    /**
     * 交易规则过滤责任链装配
     * 这里注意一下Spring的注入方式
     *
     * @param activityEffectiveRuleFilter 活动生效规则过滤
     * @param userTakeLimitRuleFilter     用户参与次数规则过滤
     * @return 规则过滤责任链
     */
    @Bean("tradeRuleFilterChain")
    public BusinessLinkedList<LockRuleFilterParamsEntity, LockRuleFilterContext, LockRuleFilterRespEntity> tradeRuleFilterChain(ActivityEffectiveRuleFilter activityEffectiveRuleFilter, UserTakeLimitRuleFilter userTakeLimitRuleFilter) {
        LinkArmory<LockRuleFilterParamsEntity, LockRuleFilterContext, LockRuleFilterRespEntity> linkArmory = new LinkArmory<>(RULE_FILTER_CHAIN_NAME, activityEffectiveRuleFilter, userTakeLimitRuleFilter);
        log.info("[责任链工厂] {} 装配完成", RULE_FILTER_CHAIN_NAME);
        return linkArmory.getLogicLink();
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LockRuleFilterContext {
        private GroupBuyActivityEntity activity;
        private Integer userTakeCount;
    }
}
