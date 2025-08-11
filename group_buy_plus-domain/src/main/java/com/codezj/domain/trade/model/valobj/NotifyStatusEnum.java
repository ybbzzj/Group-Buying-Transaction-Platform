package com.codezj.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 回调状态枚举
 * 0初始、1完成、2重试、3失败
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotifyStatusEnum {
    CREATE(0, "初始"),
    SUCCESS(1, "完成"),
    RETRY(2, "重试"),
    FAIL(3, "失败")
    ;
    private Integer code;
    private String desc;
}
