package com.codezj.test.domain.tag.service;

import com.codezj.domain.tag.service.ITagService;
import com.codezj.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBitSet;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 测试人群标签服务
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TagServiceTest {

    @Resource
    private ITagService tagService;

    @Resource
    private IRedisService redisService;

    @Test
    public void testExecuteTagJob() {
        tagService.executeTagJob("RQ_KJHKL98UU78H66554GFDV", "10002");


    }

    @Test
    public void testRedisService() {
        RBitSet bitSet = redisService.getBitSet("RQ_KJHKL98UU78H66554GFDV");

        log.info("zhangsan是否具有人群标签RQ_KJHKL98UU78H66554GFDV：{}，预期为：true", bitSet.get(redisService.getIndexFromUserId("zhangsan")));
        log.info("lisi是否具有人群标签RQ_KJHKL98UU78H66554GFDV：{}，预期为：true", bitSet.get(redisService.getIndexFromUserId("lisi")));
        log.info("zhaoliu是否具有人群标签RQ_KJHKL98UU78H66554GFDV：{}，预期为：true", bitSet.get(redisService.getIndexFromUserId("zhaoliu")));

        log.info("hajimi是否具有人群标签RQ_KJHKL98UU78H66554GFDV：{}，预期为：false", bitSet.get(redisService.getIndexFromUserId("hajimi")));
        log.info("damuji是否具有人群标签RQ_KJHKL98UU78H66554GFDV：{}，预期为：false", bitSet.get(redisService.getIndexFromUserId("damuji")));
        log.info("dingdongji是否具有人群标签RQ_KJHKL98UU78H66554GFDV：{}，预期为：true", bitSet.get(redisService.getIndexFromUserId("dingdongji")));
    }
}
