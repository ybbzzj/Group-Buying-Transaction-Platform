package com.codezj.api;

import com.codezj.api.dto.GoodsMarketRequestDTO;
import com.codezj.api.dto.GoodsMarketResponseDTO;
import com.codezj.api.response.Response;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 拼团首页服务接口
 **/
public interface IMarketIndexService {
    /**
     * 查询拼单首页配置
     * @param goodsMarketRequestDTO 查询条件
     * @return 拼单首页配置
     */
    Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(GoodsMarketRequestDTO goodsMarketRequestDTO);
}
