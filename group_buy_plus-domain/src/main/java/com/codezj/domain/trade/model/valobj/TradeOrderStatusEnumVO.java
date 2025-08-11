package com.codezj.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 订单状态枚举
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TradeOrderStatusEnumVO {

    CREATE(0, "创建"),
    COMPLETE(1, "完成"),
    CANCEL(2, "取消")
    ;

    private Integer code;
    private String desc;

    public static TradeOrderStatusEnumVO getByCode(Integer code) {
        switch (code) {
            case 0:
                return CREATE;
            case 1:
                return COMPLETE;
            case 2:
                return CANCEL;
            default:
                throw new RuntimeException("订单状态异常");
        }
    }
}
