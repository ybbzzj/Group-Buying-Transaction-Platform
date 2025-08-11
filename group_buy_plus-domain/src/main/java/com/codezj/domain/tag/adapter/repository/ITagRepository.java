package com.codezj.domain.tag.adapter.repository;

import com.codezj.domain.tag.model.entity.CrowdTagsJobEntity;

/**
 * tag领域仓储适配器接口
 */
public interface ITagRepository {
    /**
     * 查询标签任务
     */
    CrowdTagsJobEntity queryCrowdTagsJob(String tagId, String batchId);

    /**
     * 添加人群标签明细
     * @return 影响行数
     */
    int addCrowdTagsDetail(String tagId, String userId);

    /**
     * 更新标签信息
     */
    void updateCrowdTagsStatistics(String tagId, int count);
}
