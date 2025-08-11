package com.codezj.trigger.http;

import com.alibaba.fastjson.JSON;
import com.codezj.api.dto.NotifyRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: 测试回调接口
 **/
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/test/")
public class TestApiClientController {

    /**
     * 模拟回调案例
     *
     * @param notifyRequestDTO 通知回调参数
     * @return success 成功，error 失败
     */
    @RequestMapping(value = "group_buy_notify", method = RequestMethod.POST)
    public String groupBuyNotify(@RequestBody NotifyRequestDTO notifyRequestDTO) {
        log.info("模拟测试第三方服务接收拼团回调 {}", JSON.toJSONString(notifyRequestDTO));

        return "success";
    }

}
