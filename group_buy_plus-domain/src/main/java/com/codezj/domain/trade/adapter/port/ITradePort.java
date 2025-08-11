package com.codezj.domain.trade.adapter.port;

import com.codezj.domain.trade.model.entity.NotifyTaskEntity;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 交易端口
 **/
public interface ITradePort {
    String groupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception;
}
