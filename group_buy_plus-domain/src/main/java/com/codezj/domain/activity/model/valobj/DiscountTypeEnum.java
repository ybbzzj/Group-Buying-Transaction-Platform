package com.codezj.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum DiscountTypeEnum {
    BASE(0, "基础优惠"),
    TAG(1, "人群标签");

    private Integer code;
    private String desc;

    public static DiscountTypeEnum getDiscountTypeEnum(Integer code) throws Exception {
        switch (code) {
            case 0:
                return BASE;
            case 1:
                return TAG;
            default:
                throw new RuntimeException("优惠类型错误");
        }
    }
}
