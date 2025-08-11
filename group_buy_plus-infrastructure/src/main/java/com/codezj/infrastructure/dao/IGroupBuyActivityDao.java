package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.GroupBuyActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author baozhongjie
 * @description 活动配置mapper
 */
@Mapper
public interface IGroupBuyActivityDao {

    int insert(GroupBuyActivity groupBuyActivity);

    List<GroupBuyActivity> queryGroupBuyActivityList();

    /**
     * 查询有效的活动，limit 1
     */
    GroupBuyActivity queryValidGroupBuyActivity(GroupBuyActivity groupBuyActivityReq);

    GroupBuyActivity queryGroupBuyActivityByActivityId(Long activityId);

}
