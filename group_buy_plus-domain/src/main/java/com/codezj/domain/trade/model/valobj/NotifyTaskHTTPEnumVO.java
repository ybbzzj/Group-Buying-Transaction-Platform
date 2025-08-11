package com.codezj.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: TODO
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotifyTaskHTTPEnumVO {
    SUCCESS("success", "成功"),
    ERROR("error", "失败")
    ;

    private String code;
    private String msg;

    public static NotifyTaskHTTPEnumVO getByCode(String code) {
        for (NotifyTaskHTTPEnumVO value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("NotifyTaskHTTPEnumVO not found by code: " + code);
    }
}
