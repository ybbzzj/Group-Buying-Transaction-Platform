package com.codezj.test.infrastructure.rcc;

import com.codezj.api.IRCCService;
import com.codezj.api.dto.RCCUpdateRequest;
import com.codezj.api.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: RCC服务测试
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RCCServiceTest {

    @Resource
    private IRCCService rccService;

    @Test
    public void testUpdateRCCValue() {
        Response<Boolean> response = rccService.updateRCCValue(new RCCUpdateRequest("WHITELIST_MODE_SWITCH", "0"));
        log.info("测试结果: {}", response);
    }
}
