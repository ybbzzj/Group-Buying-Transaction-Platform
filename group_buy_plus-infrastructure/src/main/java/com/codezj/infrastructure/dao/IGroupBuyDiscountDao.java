package com.codezj.infrastructure.dao;

import com.codezj.infrastructure.dao.po.GroupBuyDiscount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author baozhongjie
 * @desription 折扣配置mapper
 */
@Mapper
public interface IGroupBuyDiscountDao {

    List<GroupBuyDiscount> queryGroupBuyDiscountList();

    /**
     * 根据折扣ID查询折扣信息
     */
    GroupBuyDiscount queryGroupBuyActivityDiscountByDiscountId(Long discountId);
}
