package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.SCSkuActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 商品活动关联信息Mapper
 **/
@Mapper
public interface ISCSkuActivityDao {

    int insert(SCSkuActivity scSkuActivity);

    SCSkuActivity querySCSkuActivityBySCGoodsId(SCSkuActivity scSkuActivityReq);

}
