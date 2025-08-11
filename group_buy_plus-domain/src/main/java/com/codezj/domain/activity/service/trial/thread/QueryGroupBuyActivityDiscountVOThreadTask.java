package com.codezj.domain.activity.service.trial.thread;

import com.codezj.domain.activity.adapter.repository.IActivityRepository;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SCSkuActivityVO;

import java.util.Objects;
import java.util.concurrent.Callable;

public class QueryGroupBuyActivityDiscountVOThreadTask implements Callable<GroupBuyActivityDiscountVO> {

    private final String source;

    private final String channel;

    private final String goodsId;

    private final IActivityRepository activityRepository;

    public QueryGroupBuyActivityDiscountVOThreadTask(String source, String channel, String goodsId, IActivityRepository activityRepository) {
        this.source = source;
        this.channel = channel;
        this.goodsId = goodsId;
        this.activityRepository = activityRepository;
    }

    @Override
    public GroupBuyActivityDiscountVO call() throws Exception {
        SCSkuActivityVO scSkuActivityVO = activityRepository.queryActivityBySCGoodsId(source, channel, goodsId);
        if (Objects.isNull(scSkuActivityVO)) {
            return null;
        }
        return activityRepository.queryGroupBuyActivityDiscountVO(scSkuActivityVO.getActivityId());
    }
}
