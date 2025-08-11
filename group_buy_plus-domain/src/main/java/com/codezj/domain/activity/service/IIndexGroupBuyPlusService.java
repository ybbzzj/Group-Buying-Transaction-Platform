package com.codezj.domain.activity.service;

import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.codezj.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * 首页拼单+活动试算接口
 */
public interface IIndexGroupBuyPlusService {

    TrialBalanceEntity indexGroupBuyPlusTrial(MarketProductEntity marketProductEntity) throws Exception;

    /**
     * 查询活动组队统计
     * @param activityId 活动ID
     * @return 活动组队统计
     */
    TeamStatisticVO queryTeamStatisticByActivityId(String goodsId);

    /**
     * 查询进行中的拼团订单详情
     *
     * @param goodsId    商品ID
     * @param userId     用户ID
     * @param ownerCount 团长数量
     * @param randomCount 随机数量
     * @return 进行中的拼团订单详情列表
     */
    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(String goodsId, String userId, Integer ownerCount, Integer randomCount);
}
