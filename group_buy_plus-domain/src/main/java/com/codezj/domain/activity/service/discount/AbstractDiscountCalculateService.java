package com.codezj.domain.activity.service.discount;

import com.codezj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static com.codezj.types.common.Constants.SPLIT;

@Slf4j
public abstract class AbstractDiscountCalculateService implements IDiscountCalculateService {

    @Override
    public BigDecimal calculateDiscount(String userId, BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception {
        // 营销优惠表达式合法性判断
        Boolean validMarketExpr = isValidMarketExpr(originPrice, groupBuyDiscount);
        if (!validMarketExpr) {
            return originPrice;
        }

        return doCalculate(originPrice, groupBuyDiscount);

    }

    private Boolean isValidMarketExpr(BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        String marketExpr = groupBuyDiscount.getMarketExpr();
        if (StringUtils.isBlank(marketExpr)) {
            log.error("[优惠计算服务]营销优惠表达式不合法，表达式为空");
            return false;
        }
        switch (groupBuyDiscount.getMarketPlan()) {
            case "MJ": {
                String[] split = marketExpr.split(SPLIT);
                if (split.length != 2 ||
                    !StringUtils.isNumeric(split[0]) ||
                    !StringUtils.isNumeric(split[1]) ||
                    split[0].compareTo(split[1]) < 0 ||
                    split[1].compareTo("0") <= 0) {
                    log.error("[优惠计算服务]满减策略配置有误，表达式格式不合法");
                    return false;
                }
                break;
            }
            case "ZK": {
                BigDecimal discount = new BigDecimal(marketExpr);
                if (discount.compareTo(BigDecimal.ZERO) <= 0 || discount.compareTo(BigDecimal.ONE) >= 0) {
                    log.error("[优惠计算服务]折扣策略配置有误，折扣率不合法");
                    return false;
                }
                break;
            }
            case "ZJ": {
                BigDecimal discount = new BigDecimal(marketExpr);
                if (discount.compareTo(BigDecimal.ZERO) <= 0 || discount.compareTo(originPrice) >= 0) {
                    log.error("[优惠计算服务]直减策略配置有误，直降金额不合法");
                    return false;
                }
                break;
            }
            case "N": {
                BigDecimal discount = new BigDecimal(marketExpr);
                if (discount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.error("[优惠计算服务]N元策略配置有误，参数不合法");
                    return false;
                }
                break;
            }
            default:
                break;
        }
        return true;

    }


    protected abstract BigDecimal doCalculate(BigDecimal originPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) throws Exception;
}
