package com.codezj.test.infrastructure.dao;

import com.codezj.infrastructure.dao.ICrowdTagsDao;
import com.codezj.infrastructure.dao.ICrowdTagsDetailDao;
import com.codezj.infrastructure.dao.ICrowdTagsJobDao;
import com.codezj.infrastructure.dao.po.CrowdTags;
import com.codezj.infrastructure.dao.po.CrowdTagsDetail;
import com.codezj.infrastructure.dao.po.CrowdTagsJob;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrowdTagsDaoTest {

    @Resource
    private ICrowdTagsDao crowdTagsDao;

    @Resource
    private ICrowdTagsDetailDao crowdTagsDetailDao;

    @Resource
    private ICrowdTagsJobDao crowdTagsJobDao;


    @Test
    public void testUpdateCrowdTagsStatistics() {
        CrowdTags crowdTagsReq = CrowdTags.builder().tagId("RQ_KJHKL98UU78H66554GFDV").statistics(10).build();
        crowdTagsDao.updateCrowdTagsStatistics(crowdTagsReq);
    }

    @Test
    public void testAddCrowdTagsUserId() {
        int add = 0;
        try {
            CrowdTagsDetail crowdTagsDetail = CrowdTagsDetail.builder().tagId("RQ_KJHKL98UU78H66554GFDV").userId("dingdongji").build();
            add = crowdTagsDetailDao.addCrowdTagsUserId(crowdTagsDetail);
            log.info("受影响的行数: {}", add);
        } catch (DuplicateKeyException ignore) {
            log.info("用户ID已存在");
        }


    }

    @Test
    public void testQueryCrowdTagsJob() {
        CrowdTagsJob crowdTagsJobReq = CrowdTagsJob.builder().tagId("RQ_KJHKL98UU78H66554GFDV").batchId("10001").build();
        CrowdTagsJob crowdTagsJob = crowdTagsJobDao.queryCrowdTagsJob(crowdTagsJobReq);
        log.info("标签任务查询成功，crowdTagsJob:{}", crowdTagsJob);
    }
}
