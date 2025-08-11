package com.codezj.types.enums;

import com.codezj.types.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 活动状态枚举
 * 活动状态（0创建、1生效、2过期、3废弃）
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ActivityStatusEnum {
    CREATE(0, "创建"),
    EFFECTIVE(1, "生效"),
    EXPIRE(2, "过期"),
    DISCARD(3, "废弃")
    ;


    private Integer code;
    private String desc;

    public static ActivityStatusEnum getByCode(Integer code) {
        for (ActivityStatusEnum value : ActivityStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException("活动状态枚举转换异常");
    }


}
