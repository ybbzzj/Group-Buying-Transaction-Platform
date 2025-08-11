package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.GroupBuyOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼团订单mapper
 **/
@Mapper
public interface IGroupBuyOrderDao {

    /**
     * 插入拼团订单
     */
    int insert(GroupBuyOrder groupBuyOrder);

    /**
     * 增加锁单数量
     */
    int updateAddLockCount(String teamId);

    /**
     * 减少锁单数量
     */
    int updateSubLockCount(String teamId);

    /**
     * 查询拼团进度
     */
    GroupBuyOrder queryGroupBuyProgressByTeamId(String teamId);

    /**
     * 查询拼团订单
     */
    GroupBuyOrder queryGroupBuyByTeamId(String teamId);

    /**
     * 增加拼团完成数量
     */
    int updateAddCompleteCount(String teamId);

    /**
     * 更新拼团订单状态
     */
    int updateGroupBuyOrderStatus2COMPLETE(String teamId);

    /**
     * 查询拼团进行中的订单
     */
    List<GroupBuyOrder> queryGroupBuyProgressByTeamIds(@Param("teamIds") Set<String> teamIds);

    /**
     * 查询拼团进行中的订单数量
     */
    Integer queryAllTeamCount(@Param("teamIds") Set<String> teamIds);

    /**
     * 查询拼团进行中的订单完成数量
     */
    Integer queryAllTeamCompleteCount(@Param("teamIds") Set<String> teamIds);

    /**
     * 查询拼团进行中的订单用户数量
     */
    Integer queryAllUserCount(@Param("teamIds") Set<String> teamIds);
}
