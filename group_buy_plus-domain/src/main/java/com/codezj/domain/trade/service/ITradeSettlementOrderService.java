package com.codezj.domain.trade.service;

import com.codezj.domain.trade.model.entity.SettlementReqEntity;
import com.codezj.domain.trade.model.entity.SettlementRespEntity;

import java.util.Map;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 结算服务接口
 **/
public interface ITradeSettlementOrderService {

    /**
     * 执行拼团结算
     *
     * @param settlementReqEntity 结算请求实体
     * @return 结算响应实体
     */
    SettlementRespEntity executeSettlement(SettlementReqEntity settlementReqEntity) throws Exception;

    /**
     * 执行结算通知 job
     *
     * @return 结算通知结果
     */
    Map<String, Integer> execSettlementNotifyJob() throws Exception;

    /**
     * 执行结算通知 job
     *
     * @param teamId 拼团ID
     * @return 结算通知结果
     */
    Map<String, Integer> execSettlementNotifyJob(String teamId) throws Exception;
}
