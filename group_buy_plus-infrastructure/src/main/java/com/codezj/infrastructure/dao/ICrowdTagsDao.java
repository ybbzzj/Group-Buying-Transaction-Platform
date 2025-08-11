package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.CrowdTags;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人群标签Mapper
 */
@Mapper
public interface ICrowdTagsDao {

    int insert(CrowdTags crowdTags);

    /**
     * 更新人群标签统计数据
     */
    void updateCrowdTagsStatistics(CrowdTags crowdTagsReq);

}