package com.codezj.domain.activity.service;

import com.codezj.domain.activity.adapter.repository.IActivityRepository;
import com.codezj.domain.activity.model.entity.MarketProductEntity;
import com.codezj.domain.activity.model.entity.TrialBalanceEntity;
import com.codezj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.codezj.domain.activity.model.valobj.TeamStatisticVO;
import com.codezj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.codezj.types.design.framework.tree.StrategyHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页拼单+活动实现
 */
@Service
public class IndexGroupBuyPlusServiceImpl implements IIndexGroupBuyPlusService {

    @Resource
    private DefaultActivityStrategyFactory defaultActivityStrategyFactory;

    @Resource
    private IActivityRepository repository;

    @Override
    public TrialBalanceEntity indexGroupBuyPlusTrial(MarketProductEntity marketProductEntity) throws Exception {
        StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.GroupBuyContext, TrialBalanceEntity> strategyHandler = defaultActivityStrategyFactory.strategyHandler();
        TrialBalanceEntity trialBalance = strategyHandler.handle(marketProductEntity, new DefaultActivityStrategyFactory.GroupBuyContext());
        return trialBalance;
    }

    @Override
    public TeamStatisticVO queryTeamStatisticByActivityId(String goodsId) {
        return repository.queryTeamStatisticByActivityId(goodsId);
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(String goodsId, String userId, Integer ownerCount, Integer randomCount) {
        List<UserGroupBuyOrderDetailEntity> unionAllList = new ArrayList<>();

        // 查询个人拼团数据
        if (0 != ownerCount) {
            List<UserGroupBuyOrderDetailEntity> ownerList = repository.queryInProgressUserGroupBuyOrderDetailListByOwner(goodsId, userId, ownerCount);
            if (null != ownerList && !ownerList.isEmpty()){
                unionAllList.addAll(ownerList);
            }
        }

        // 查询其他非个人拼团
        if (0 != randomCount) {
            List<UserGroupBuyOrderDetailEntity> randomList = repository.queryInProgressUserGroupBuyOrderDetailListByRandom(goodsId, userId, randomCount);
            if (null != randomList && !randomList.isEmpty()){
                unionAllList.addAll(randomList);
            }
        }

        return unionAllList;
    }
}
