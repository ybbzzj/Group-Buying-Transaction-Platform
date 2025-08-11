package com.codezj.domain.trade.service.lock.filter.impl;

import com.codezj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterParamsEntity;
import com.codezj.domain.trade.model.entity.LockRuleFilterRespEntity;
import com.codezj.domain.trade.service.lock.factory.TraderLockRuleFilterFactory;
import com.codezj.domain.trade.service.lock.filter.AbstractLockRuleFilter;
import com.codezj.types.enums.ActivityStatusEnum;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 活动生效规则过滤器
 **/
@Slf4j
@Service
public class ActivityEffectiveRuleFilter extends AbstractLockRuleFilter {



    @Override
    public LockRuleFilterRespEntity apply(LockRuleFilterParamsEntity requestParameter, TraderLockRuleFilterFactory.LockRuleFilterContext dynamicContext) throws Exception {
        log.info("[规则过滤器] ActivityEffectiveRuleFilter 开始执行");

        // 获取活动的详情
        GroupBuyActivityEntity groupBuyActivityEntity = repository.getGroupBuyActivity(requestParameter.getActivityId());

        // 判断活动的状态
        if (!ActivityStatusEnum.EFFECTIVE.equals(groupBuyActivityEntity.getStatus())) {
            throw new AppException(ResponseCode.E0101.getCode(), ResponseCode.E0101.getInfo());
        }

        // 判断活动时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(groupBuyActivityEntity.getStartTime()) || now.isAfter(groupBuyActivityEntity.getEndTime())) {
            throw new AppException(ResponseCode.E0102.getCode(), ResponseCode.E0102.getInfo());
        }

        // 上下文设置活动信息
        dynamicContext.setActivity(groupBuyActivityEntity);

        log.info("[规则过滤器] ActivityEffectiveRuleFilter 执行结束");
        return next(requestParameter, dynamicContext);
    }
}
