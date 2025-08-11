package com.codezj.domain.trade.adapter.repository;

import com.codezj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.codezj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.codezj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.codezj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.codezj.domain.trade.model.entity.MarketPayOrderEntity;
import com.codezj.domain.trade.model.entity.NotifyTaskEntity;
import com.codezj.domain.trade.model.valobj.TradeOrderProgressVO;

import java.util.List;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易仓储接口
 **/
public interface ITradeRepository {
    /**
     * 查询用户未支付的拼团订单
     */
    MarketPayOrderEntity queryNotPayOrderByOutTradeNo(String userId, String outTradeNo);

    /**
     * 查询拼团订单进度
     */
    TradeOrderProgressVO queryTradeOrderProgress(String teamId);

    /**
     * 锁定拼团订单
     */
    MarketPayOrderEntity lockGroupBuyTradeOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) throws Exception;

    /**
     * 查询拼团活动
     */
    GroupBuyActivityEntity getGroupBuyActivity(Long activityId);

    /**
     * 查询用户参与拼团次数
     */
    Integer queryUserTakeCountByActivityId(String userId, Long activityId);

    /**
     * 更新支付订单状态->完成
     */
    Integer updatePayOrderStatus2COMPLETE(String userId, String outTradeNo, Integer status);

    /**
     * 查询拼团
     */
    GroupBuyTeamEntity queryGroupBuyTeam(String teamId);

    /**
     * 结算拼团订单
     */
    void settleGroupBuyOrder(GroupBuyTeamSettlementAggregate groupBuyTeamSettlementAggregate);

    /**
     * 判断是否屏蔽SC
     */
    boolean isInSCBlockList(String source, String channel);

    /**
     * 查询未执行任务
     */
    List<NotifyTaskEntity> queryUnExecutedNotifyTaskList();

    /**
     * 查询未执行任务
     */
    List<NotifyTaskEntity> queryUnExecutedNotifyTaskList(String teamId);

    /**
     * 更新任务状态为成功
     */
    int updateNotifyTaskStatusSuccess(String teamId);

    /**
     * 更新任务状态为失败
     */
    int updateNotifyTaskStatusError(String teamId);

    /**
     * 更新任务状态为重试
     */
    int updateNotifyTaskStatusRetry(String teamId);
}
