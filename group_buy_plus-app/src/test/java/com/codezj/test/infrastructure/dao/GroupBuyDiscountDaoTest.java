package com.codezj.test.infrastructure.dao;

import com.alibaba.fastjson.JSON;
import com.codezj.infrastructure.dao.IGroupBuyDiscountDao;
import com.codezj.infrastructure.dao.po.GroupBuyDiscount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupBuyDiscountDaoTest {

    @Resource
    private IGroupBuyDiscountDao groupBuyDiscountDao;

    @Test
    public void testQueryGroupBuyActivity() {
        List<GroupBuyDiscount> groupBuyDiscountList = groupBuyDiscountDao.queryGroupBuyDiscountList();
        System.out.println("测试结果： " + JSON.toJSONString(groupBuyDiscountList));
    }
}
