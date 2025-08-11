package com.codezj.test.infrastructure.dao;

import com.codezj.infrastructure.dao.INotifyTaskDao;
import com.codezj.infrastructure.dao.po.NotifyTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 回调通知任务测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class NotifyTaskDaoTest {

    @Resource
    private INotifyTaskDao notifyTaskDao;

    @Test
    public void testInsert() {
        NotifyTask notifyTask = NotifyTask.builder()
                .activityId(1L)
                .notifyUrl("http://www.codezj.top/")
                .notifyCount(1)
                .notifyStatus(1)
                .parameterJson("{}")
                .teamId("12345668")
                .build();
        notifyTaskDao.insert(notifyTask);


    }
}
