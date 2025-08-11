package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.GroupBuyOrderList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团订单列表mapper
 **/
@Mapper
public interface IGroupBuyOrderListDao {

    /**
     * 插入拼团订单
     */
    int insert(GroupBuyOrderList groupBuyOrderList);

    /**
     * 根据外部订单号查询拼团订单
     */
    GroupBuyOrderList queryGroupBuyOrderRecordByOutTradeNo(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 查询用户参与的拼团订单
     */
    GroupBuyOrderList queryGroupBuyOrderRecordByUserIdAndTeamId(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 查询用户参与拼团订单数量
     */
    Integer queryUserTakeCountByActivityId(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 更新订单状态->完成支付
     */
    Integer updateOrderStatus2COMPLETE(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 查询拼团完成的订单号
     */
    List<String> queryGroupBuyCompleteOutOrderNosByTeamId(String teamId);

    /**
     * 查询用户进行中的拼团订单
     */
    List<GroupBuyOrderList> queryInProgressUserGroupBuyOrderDetailListByUserId(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 查询非用户进行中的拼团订单-随机
     */
    List<GroupBuyOrderList> queryInProgressUserGroupBuyOrderDetailListByRandom(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 根据商品ID查询进行中的拼团订单
     */
    List<GroupBuyOrderList> queryInProgressUserGroupBuyOrderDetailListByGoodsId(String goodsId);
}
