package com.codezj.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易订单进度
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeOrderProgressVO {

    /** 目标数量 */
    private Integer targetCount;
    /** 完成数量 */
    private Integer completeCount;
    /** 锁单数量 */
    private Integer lockCount;
}
