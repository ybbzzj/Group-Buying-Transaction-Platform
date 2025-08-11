package com.codezj.domain.activity.service.trial.thread;

import com.codezj.domain.activity.adapter.repository.IActivityRepository;
import com.codezj.domain.activity.model.valobj.SkuVO;

import java.util.concurrent.Callable;

public class QuerySkuVOFromDBThreadTask implements Callable<SkuVO> {

    private final String goodsId;

    private final String source;

    private final String channel;

    private final IActivityRepository activityRepository;

    public QuerySkuVOFromDBThreadTask(String goodsId, String source, String channel, IActivityRepository activityRepository) {
        this.goodsId = goodsId;
        this.source = source;
        this.channel = channel;
        this.activityRepository = activityRepository;
    }


    @Override
    public SkuVO call() throws Exception {
        return activityRepository.querySkuByGoodsId(goodsId, source, channel);
    }
}
