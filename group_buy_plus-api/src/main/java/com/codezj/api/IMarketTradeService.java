package com.codezj.api;

import com.codezj.api.dto.LockMarketPayOrderRequestDTO;
import com.codezj.api.dto.LockMarketPayOrderResponseDTO;
import com.codezj.api.dto.SettlementMarketPayOrderRequestDTO;
import com.codezj.api.dto.SettlementMarketPayOrderResponseDTO;
import com.codezj.api.response.Response;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 交易服务接口
 **/
public interface IMarketTradeService {

    /**
     * 加锁订单
     */
    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO);

    /**
     * 结算订单
     */
    public Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(SettlementMarketPayOrderRequestDTO requestDTO);
}
