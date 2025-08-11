package com.codezj.trigger.http;

import com.codezj.api.IRCCService;
import com.codezj.api.dto.RCCUpdateRequest;
import com.codezj.api.response.Response;
import com.codezj.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: RccController
 **/
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/gbp/rcc")
public class RCCController implements IRCCService {

    @Resource
    private RTopic dccTopic;

    /**
     * 更新RCC配置
     */
    @Override
    @PostMapping({"", "/update-rcc-value"})
    public Response<Boolean> updateRCCValue(@RequestBody RCCUpdateRequest rccUpdateRequest) {
        if (Objects.isNull(rccUpdateRequest)) {
            return Response.<Boolean>builder()
                    .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                    .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                    .data(false)
                    .build();
        }
        String rccKey = rccUpdateRequest.getRccKey();
        String rccValue = rccUpdateRequest.getRccValue();
        try {
            log.info("[Controller] RCC配置更新。rccKey:{}, rccValue:{}", rccKey, rccValue);
            dccTopic.publish(rccKey + "," + rccValue);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("[Controller] RCC配置更新失败。key:{} value:{}", rccKey, rccValue, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }

    }
}
