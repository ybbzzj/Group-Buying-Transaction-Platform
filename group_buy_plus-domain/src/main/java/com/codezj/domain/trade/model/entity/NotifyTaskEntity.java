package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 通知任务实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyTaskEntity {
    /** 活动ID */
    private Long activityId;
    /** 拼单组队ID */
    private String teamId;
    /** 回调接口 */
    private String notifyUrl;
    /** 回调次数 */
    private Integer notifyCount;
    /** 回调状态【0初始、1完成、2重试、3失败】 */
    private Integer notifyStatus;
    /**  */
    private String parameterJson;
}
