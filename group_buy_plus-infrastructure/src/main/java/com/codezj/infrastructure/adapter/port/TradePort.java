package com.codezj.infrastructure.adapter.port;

import com.codezj.domain.trade.adapter.port.ITradePort;
import com.codezj.domain.trade.model.entity.NotifyTaskEntity;
import com.codezj.infrastructure.gateway.GroupBuyNotifyService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: TODO
 **/
@Component
public class TradePort implements ITradePort {

    @Resource
    private GroupBuyNotifyService groupBuyNotifyService;

    @Override
    public String groupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception{


        return groupBuyNotifyService.groupBuyNotify(notifyTaskEntity.getNotifyUrl(), notifyTaskEntity.getParameterJson());
    }
}
