package com.codezj.test.infrastructure.dao;

import com.alibaba.fastjson.JSON;
import com.codezj.infrastructure.dao.IGroupBuyActivityDao;
import com.codezj.infrastructure.dao.po.GroupBuyActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupBuyActivityDaoTest {

    @Resource
    private IGroupBuyActivityDao groupBuyActivityDao;

    @Test
    public void testQueryGroupBuyActivity() {
        List<GroupBuyActivity> groupBuyActivityList = groupBuyActivityDao.queryGroupBuyActivityList();
        System.out.println("测试结果： " + JSON.toJSONString(groupBuyActivityList));
    }
}
