package com.codezj.trigger.http;

import com.alibaba.fastjson.JSON;
import com.codezj.api.IMarketTradeService;
import com.codezj.api.dto.LockMarketPayOrderRequestDTO;
import com.codezj.api.dto.LockMarketPayOrderResponseDTO;
import com.codezj.api.dto.SettlementMarketPayOrderRequestDTO;
import com.codezj.api.dto.SettlementMarketPayOrderResponseDTO;
import com.codezj.api.response.Response;
import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.codezj.domain.activity.service.IIndexGroupBuyPlusService;
import com.codezj.domain.trade.model.entity.*;
import com.codezj.domain.trade.model.valobj.TradeOrderProgressVO;
import com.codezj.domain.trade.service.ITradeLockOrderService;
import com.codezj.domain.trade.service.ITradeSettlementOrderService;
import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 营销交易Controller
 **/
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbp/trade/")
public class MarketTradeController implements IMarketTradeService {

    @Resource
    private IIndexGroupBuyPlusService indexGroupBuyPlusService;

    @Resource
    private ITradeLockOrderService tradeService;

    @Resource
    private ITradeSettlementOrderService tradeSettlementOrderService;

    /**
     * 拼团营销锁单
     */
    @PostMapping({"", "/lock-pay-order"})
    @Override
    public Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(@RequestBody LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO) {
        try {
            // 参数
            String userId = lockMarketPayOrderRequestDTO.getUserId();
            String source = lockMarketPayOrderRequestDTO.getSource();
            String channel = lockMarketPayOrderRequestDTO.getChannel();
            String goodsId = lockMarketPayOrderRequestDTO.getGoodsId();
            Long activityId = lockMarketPayOrderRequestDTO.getActivityId();
            String outTradeNo = lockMarketPayOrderRequestDTO.getOutTradeNo();
            String teamId = lockMarketPayOrderRequestDTO.getTeamId();
            String notifyUrl = lockMarketPayOrderRequestDTO.getNotifyUrl();

            log.info("营销交易锁单:{} LockMarketPayOrderRequestDTO:{}", userId, JSON.toJSONString(lockMarketPayOrderRequestDTO));

            if (StringUtils.isBlank(userId) || StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(goodsId) || StringUtils.isBlank(goodsId) || null == activityId) {
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 查询 outTradeNo 是否已经存在交易记录
            MarketPayOrderEntity marketPayOrderEntity = tradeService.queryNotPayOrderByOutTradeNo(userId, outTradeNo);
            if (null != marketPayOrderEntity) {
                LockMarketPayOrderResponseDTO lockMarketPayOrderResponseDTO = LockMarketPayOrderResponseDTO.builder()
                        .orderId(marketPayOrderEntity.getOrderId())
                        .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                        .tradeOrderStatus(marketPayOrderEntity.getTradeOrderStatusEnumVO().getCode())
                        .build();

                log.info("交易锁单记录(存在):{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.SUCCESS.getCode())
                        .info(ResponseCode.SUCCESS.getInfo())
                        .data(lockMarketPayOrderResponseDTO)
                        .build();
            }

            // 判断拼团锁单是否完成了目标
            if (null != teamId) {
                TradeOrderProgressVO tradeOrderProgressVO = tradeService.queryTradeOrderProgress(teamId);
                if (null != tradeOrderProgressVO && Objects.equals(tradeOrderProgressVO.getTargetCount(), tradeOrderProgressVO.getLockCount())) {
                    log.info("交易锁单拦截-拼单目标已达成:{} {}", userId, teamId);
                    return Response.<LockMarketPayOrderResponseDTO>builder()
                            .code(ResponseCode.E0006.getCode())
                            .info(ResponseCode.E0006.getInfo())
                            .build();
                }
            }

            // 营销优惠试算
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(MarketProductEntity.builder()
                    .userId(userId)
                    .source(source)
                    .channel(channel)
                    .goodsId(goodsId)
                    .activityId(activityId)
                    .build());

            // 人群限定
            if (!trialBalanceEntity.getIsEnable()){
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.E0007.getCode())
                        .info(ResponseCode.E0007.getInfo())
                        .build();
            }

            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();

            // 营销优惠锁单
            try {
                marketPayOrderEntity = tradeService.lockGroupBuyTradeOrder(
                        UserEntity.builder().userId(userId).build(),
                        PayActivityEntity.builder()
                                .teamId(teamId)
                                .activityId(activityId)
                                .activityName(groupBuyActivityDiscountVO.getActivityName())
                                .startTime(groupBuyActivityDiscountVO.getStartTime())
                                .endTime(groupBuyActivityDiscountVO.getEndTime())
                                .targetCount(groupBuyActivityDiscountVO.getTarget())
                                .outTradeNo(outTradeNo)
                                .validTime(groupBuyActivityDiscountVO.getValidTime())
                                .notifyUrl(notifyUrl)
                                .build(),
                        PayDiscountEntity.builder()
                                .source(source)
                                .channel(channel)
                                .goodsId(goodsId)
                                .goodsName(trialBalanceEntity.getGoodsName())
                                .originalPrice(trialBalanceEntity.getOriginalPrice())
                                .deductionPrice(trialBalanceEntity.getDeductionPrice())
                                .payPrice(trialBalanceEntity.getPayPrice())
                                .build());
            } catch (Exception e) {
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.LOCK_ORDER_ERROR.getCode())
                        .info(e.getMessage())
                        .build();
            }

            log.info("交易锁单记录(新):{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));

            // 返回结果
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(LockMarketPayOrderResponseDTO.builder()
                            .orderId(marketPayOrderEntity.getOrderId())
                            .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                            .tradeOrderStatus(marketPayOrderEntity.getTradeOrderStatusEnumVO().getCode())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("营销交易锁单业务异常:{} LockMarketPayOrderRequestDTO:{}", lockMarketPayOrderRequestDTO.getUserId(), JSON.toJSONString(lockMarketPayOrderRequestDTO), e);
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("营销交易锁单服务失败:{} LockMarketPayOrderRequestDTO:{}", lockMarketPayOrderRequestDTO.getUserId(), JSON.toJSONString(lockMarketPayOrderRequestDTO), e);
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping({"", "/settlement_market_pay_order"})
    @Override
    public Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(@RequestBody SettlementMarketPayOrderRequestDTO requestDTO) {
        try {
            log.info("营销交易组队结算开始:{} outTradeNo:{}", requestDTO.getUserId(), requestDTO.getOutTradeNo());

            if (StringUtils.isBlank(requestDTO.getUserId()) || StringUtils.isBlank(requestDTO.getSource()) || StringUtils.isBlank(requestDTO.getChannel()) || StringUtils.isBlank(requestDTO.getOutTradeNo()) || null == requestDTO.getOutTradeTime()) {
                return Response.<SettlementMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 1. 结算服务
            SettlementRespEntity settlementRespEntity = tradeSettlementOrderService.executeSettlement(SettlementReqEntity.builder()
                    .source(requestDTO.getSource())
                    .channel(requestDTO.getChannel())
                    .userId(requestDTO.getUserId())
                    .outTradeNo(requestDTO.getOutTradeNo())
                    .outTradeTime(requestDTO.getOutTradeTime())
                    .build());

            SettlementMarketPayOrderResponseDTO responseDTO = SettlementMarketPayOrderResponseDTO.builder()
                    .userId(settlementRespEntity.getUserId())
                    .teamId(settlementRespEntity.getTeamId())
                    .activityId(settlementRespEntity.getActivityId())
                    .outTradeNo(settlementRespEntity.getOutTradeNo())
                    .build();

            // 返回结果
            Response<SettlementMarketPayOrderResponseDTO> response = Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(SettlementMarketPayOrderResponseDTO.builder()
                            .userId(settlementRespEntity.getUserId())
                            .teamId(settlementRespEntity.getTeamId())
                            .activityId(settlementRespEntity.getActivityId())
                            .outTradeNo(settlementRespEntity.getOutTradeNo())
                            .build())
                    .build();

            log.info("营销交易组队结算完成:{} outTradeNo:{} response:{}", requestDTO.getUserId(), requestDTO.getOutTradeNo(), JSON.toJSONString(response));

            return response;
        } catch (AppException e) {
            log.error("营销交易组队结算异常:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e) {
            log.error("营销交易组队结算失败:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
