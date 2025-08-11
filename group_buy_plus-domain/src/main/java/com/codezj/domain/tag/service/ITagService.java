package com.codezj.domain.tag.service;

/**
 * 人群标签服务接口
 */
public interface ITagService {

    /**
     * 执行人群标签任务
     *
     * @param tagId   标签id
     * @param batchId 批次id
     */
    void executeTagJob(String tagId, String batchId);



}
