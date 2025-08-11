package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.CrowdTagsDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人群标签明细Mapper
 */
@Mapper
public interface ICrowdTagsDetailDao {

    /**
     * 添加人群标签明细
     */
    int addCrowdTagsUserId(CrowdTagsDetail crowdTagsDetailReq);

}
