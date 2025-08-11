package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.Sku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品信息Mapper
 */
@Mapper
public interface ISkuDao {
    Sku querySkuByGoodsId(Sku sku);

}
