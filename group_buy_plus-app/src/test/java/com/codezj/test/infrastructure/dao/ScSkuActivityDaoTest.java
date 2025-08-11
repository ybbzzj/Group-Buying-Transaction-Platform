package com.codezj.test.infrastructure.dao;

import com.codezj.infrastructure.dao.ISCSkuActivityDao;
import com.codezj.infrastructure.dao.po.SCSkuActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 商品活动Dao测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScSkuActivityDaoTest {

    @Resource
    private ISCSkuActivityDao scSkuActivityDao;

    @Test
    public void testQueryActivity() {
        SCSkuActivity scSkuActivityReq = SCSkuActivity.builder()
                .source("s01")
                .channel("c01")
                .goodsId("9890001")
                .build();
        SCSkuActivity scSkuActivity = scSkuActivityDao.querySCSkuActivityBySCGoodsId(scSkuActivityReq);
        System.out.println("测试结果：" + scSkuActivity);
    }
}
