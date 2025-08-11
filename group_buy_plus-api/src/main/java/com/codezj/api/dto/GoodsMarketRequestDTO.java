package com.codezj.api.dto;

import lombok.Data;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 拼单首页请求DTO
 **/
@Data
public class GoodsMarketRequestDTO {

    // 用户ID
    private String userId;
    // 渠道
    private String source;
    // 来源
    private String channel;
    // 商品ID
    private String goodsId;

}
