package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.CrowdTagsJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人群标签任务Mapper
 */
@Mapper
public interface ICrowdTagsJobDao {

    /**
     * 查询人群标签任务
     */
    CrowdTagsJob queryCrowdTagsJob(CrowdTagsJob crowdTagsJobReq);

}