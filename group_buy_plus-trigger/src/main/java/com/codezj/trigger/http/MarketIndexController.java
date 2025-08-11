package com.codezj.trigger.http;

import com.alibaba.fastjson.JSON;
import com.codezj.api.IMarketIndexService;
import com.codezj.api.dto.GoodsMarketRequestDTO;
import com.codezj.api.dto.GoodsMarketResponseDTO;
import com.codezj.api.response.Response;
import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.codezj.domain.activity.model.valobj.TeamStatisticVO;
import com.codezj.domain.activity.service.IIndexGroupBuyPlusService;
import com.codezj.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 拼单首页
 **/
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/index/")
public class MarketIndexController implements IMarketIndexService {

    @Resource
    private IIndexGroupBuyPlusService indexGroupBuyPlusService;

    @PostMapping({"", "/query_group_buy_market_config"})
    @Override
    public Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(@RequestBody GoodsMarketRequestDTO goodsMarketRequestDTO) {
        try {
            log.info("查询拼团营销配置开始:{} goodsId:{}", goodsMarketRequestDTO.getUserId(), goodsMarketRequestDTO.getGoodsId());
            // 校验参数，用户名为空时，未登录，默认展示
            if (StringUtils.isBlank(goodsMarketRequestDTO.getSource()) || StringUtils.isBlank(goodsMarketRequestDTO.getChannel()) || StringUtils.isBlank(goodsMarketRequestDTO.getGoodsId())) {
                return Response.<GoodsMarketResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 1. 营销优惠试算
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyPlusService.indexGroupBuyPlusTrial(MarketProductEntity.builder()
                    .userId(goodsMarketRequestDTO.getUserId())
                    .source(goodsMarketRequestDTO.getSource())
                    .channel(goodsMarketRequestDTO.getChannel())
                    .goodsId(goodsMarketRequestDTO.getGoodsId())
                    .build());

            String goodsId = trialBalanceEntity.getGoodsId();

            int ownerCount = 1;
            if (StringUtils.isBlank(goodsMarketRequestDTO.getUserId())) {
                ownerCount = 0;
            }
            // 2. 查询拼团组队
            List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities = indexGroupBuyPlusService.queryInProgressUserGroupBuyOrderDetailList(goodsId, goodsMarketRequestDTO.getUserId(), ownerCount, 10);

            // 3. 统计拼团数据
            TeamStatisticVO teamStatisticVO = indexGroupBuyPlusService.queryTeamStatisticByActivityId(goodsId);

            GoodsMarketResponseDTO.Goods goods = GoodsMarketResponseDTO.Goods.builder()
                    .goodsName(trialBalanceEntity.getGoodsName())
                    .goodsId(trialBalanceEntity.getGoodsId())
                    .originalPrice(trialBalanceEntity.getOriginalPrice())
                    .deductionPrice(trialBalanceEntity.getDeductionPrice())
                    .payPrice(trialBalanceEntity.getPayPrice())
                    .build();

            GoodsMarketResponseDTO.Activity activity = GoodsMarketResponseDTO.Activity.builder()
                    .activityId(trialBalanceEntity.getGroupBuyActivityDiscountVO().getActivityId())
                    .activityName(trialBalanceEntity.getGroupBuyActivityDiscountVO().getActivityName())
                    .discountName(trialBalanceEntity.getGroupBuyActivityDiscountVO().getGroupBuyDiscount().getDiscountName())
                    .validTime(trialBalanceEntity.getGroupBuyActivityDiscountVO().getValidTime())
                    .startTime(trialBalanceEntity.getStartTime())
                    .endTime(trialBalanceEntity.getEndTime())
                    .build();

            List<GoodsMarketResponseDTO.Team> teams = new ArrayList<>();
            if (null != userGroupBuyOrderDetailEntities && !userGroupBuyOrderDetailEntities.isEmpty()) {
                for (UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity : userGroupBuyOrderDetailEntities) {
                    if (LocalDateTime.now().isAfter(userGroupBuyOrderDetailEntity.getValidEndTime())) {
                        continue;
                    }
                    GoodsMarketResponseDTO.Team team = GoodsMarketResponseDTO.Team.builder()
                            .userId(userGroupBuyOrderDetailEntity.getUserId())
                            .teamId(userGroupBuyOrderDetailEntity.getTeamId())
                            .activityId(userGroupBuyOrderDetailEntity.getActivityId())
                            .targetCount(userGroupBuyOrderDetailEntity.getTargetCount())
                            .completeCount(userGroupBuyOrderDetailEntity.getCompleteCount())
                            .lockCount(userGroupBuyOrderDetailEntity.getLockCount())
                            .validStartTime(userGroupBuyOrderDetailEntity.getValidStartTime())
                            .validEndTime(userGroupBuyOrderDetailEntity.getValidEndTime())
                            .validTimeCountdown(GoodsMarketResponseDTO.Team.differenceDateTime2Str(LocalDateTime.now(), userGroupBuyOrderDetailEntity.getValidEndTime()))
                            .outTradeNo(userGroupBuyOrderDetailEntity.getOutTradeNo())
                            .build();
                    teams.add(team);
                }
            }


            GoodsMarketResponseDTO.TeamStatistic teamStatistic = GoodsMarketResponseDTO.TeamStatistic.builder()
                    .allTeamCount(teamStatisticVO.getAllTeamCount())
                    .allTeamCompleteCount(teamStatisticVO.getAllTeamCompleteCount())
                    .allTeamUserCount(teamStatisticVO.getAllTeamUserCount())
                    .build();

            Response<GoodsMarketResponseDTO> response = Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(GoodsMarketResponseDTO.builder()
                            .goods(goods)
                            .activity(activity)
                            .teamList(teams)
                            .teamStatistic(teamStatistic)
                            .build())
                    .build();

            log.info("查询拼团营销配置完成:{} goodsId:{} response:{}", goodsMarketRequestDTO.getUserId(), goodsMarketRequestDTO.getGoodsId(), JSON.toJSONString(response));

            return response;
        } catch (Exception e) {
            log.error("查询拼团营销配置失败:{} goodsId:{}", goodsMarketRequestDTO.getUserId(), goodsMarketRequestDTO.getGoodsId(), e);
            return Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}