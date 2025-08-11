package com.codezj.domain.trade.service.lock.filter.impl;

import com.codezj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterRespEntity;
import com.codezj.domain.trade.service.lock.factory.TraderLockRuleFilterFactory;
import com.codezj.domain.trade.service.lock.filter.AbstractLockRuleFilter;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 用户参与次数规则过滤器
 **/
@Slf4j
@Service
public class UserTakeLimitRuleFilter extends AbstractLockRuleFilter {

    @Override
    public LockRuleFilterRespEntity apply(LockRuleFilterParamsEntity requestParameter, TraderLockRuleFilterFactory.LockRuleFilterContext dynamicContext) throws Exception {
        log.info("[规则过滤器] UserTakeLimitRuleFilter 开始执行");

        GroupBuyActivityEntity groupBuyActivityEntity = dynamicContext.getActivity();
        // 活动限制用户参与次数
        Integer takeLimitCount = groupBuyActivityEntity.getTakeLimitCount();
        // 查询用户参与活动的次数
        Integer userTakeCount = repository.queryUserTakeCountByActivityId(requestParameter.getUserId(), requestParameter.getActivityId());

        if (userTakeCount.compareTo(takeLimitCount) >= 0) {
            // 超过限制次数
            throw new AppException(ResponseCode.E0103.getCode(), ResponseCode.E0103.getInfo());
        }

        // 上下文设置用户参与次数
        dynamicContext.setUserTakeCount(userTakeCount);

        log.info("[规则过滤器] UserTakeLimitRuleFilter 执行结束");

        return LockRuleFilterRespEntity.builder()
                .userTakeCount(userTakeCount)
                .build();
    }
}
