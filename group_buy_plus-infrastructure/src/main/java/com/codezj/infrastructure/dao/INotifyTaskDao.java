package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.NotifyTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 回调任务DAO
 **/
@Mapper
public interface INotifyTaskDao {

    /**
     * 新增回调任务
     * @param notifyTask 回调任务
     */
    void insert(NotifyTask notifyTask);

    /**
     * 查询未执行回调任务列表
     * @return 未执行回调任务列表
     */
    List<NotifyTask> queryUnExecutedNotifyTaskList();

    /**
     * 查询未执行回调任务列表
     * @param teamId 拼单组队ID
     * @return 未执行回调任务列表
     */
    List<NotifyTask> queryUnExecutedNotifyTaskListByTeamId(String teamId);

    /**
     * 更新回调任务状态为成功
     * @param teamId 拼单组队ID
     * @return 更新行数
     */
    int updateNotifyTaskStatusSuccess(String teamId);

    /**
     * 更新回调任务状态为失败
     * @param teamId 拼单组队ID
     * @return 更新行数
     */
    int updateNotifyTaskStatusError(String teamId);

    /**
     * 更新回调任务状态为重试
     * @param teamId 拼单组队ID
     * @return 更新行数
     */
    int updateNotifyTaskStatusRetry(String teamId);
}