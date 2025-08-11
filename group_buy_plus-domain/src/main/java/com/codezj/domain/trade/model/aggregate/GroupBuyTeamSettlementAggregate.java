package com.codezj.domain.trade.model.aggregate;

import com.codezj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.codezj.domain.trade.model.entity.SettlementReqEntity;
import com.codezj.domain.trade.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 拼单结算聚合
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyTeamSettlementAggregate {


    /** 用户实体对象 */
    private UserEntity userEntity;
    /** 拼团组队实体对象 */
    private GroupBuyTeamEntity groupBuyTeamEntity;
    /** 交易支付订单实体对象 */
    private SettlementReqEntity settlementReqEntity;
}
