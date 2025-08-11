package com.codezj.domain.activity.adapter.repository;

import com.codezj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.model.valobj.SCSkuActivityVO;
import com.codezj.domain.activity.model.valobj.SkuVO;
import com.codezj.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * 活动仓储接口
 */
public interface IActivityRepository {

    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(Long activityId);

    SkuVO querySkuByGoodsId(String goodsId, String source, String channel);

    SCSkuActivityVO queryActivityBySCGoodsId(String source, String channel, String goodsId);

    // 判断用户是否具有标签
    Boolean isTagCrowdRange(String userId, String tagId);

    boolean isDowngrade();

    boolean isInCutFlowRange(String userId);

    boolean isInWhiteList(String userId);

    boolean isInWhiteList();

    /**
     * 查询用户参与的拼团进行中的订单
     */
    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByOwner(String goodsId, String userId, Integer ownerCount);

    /**
     * 查询用户未参与的拼团进行中的订单
     */
    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByRandom(String goodsId, String userId, Integer randomCount);

    TeamStatisticVO queryTeamStatisticByActivityId(String goodsId);
}
