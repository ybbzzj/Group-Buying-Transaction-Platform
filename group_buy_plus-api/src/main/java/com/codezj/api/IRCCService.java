package com.codezj.api;

import com.codezj.api.dto.RCCUpdateRequest;
import com.codezj.api.response.Response;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: RCC接口
 **/
public interface IRCCService {
    Response<Boolean> updateRCCValue(RCCUpdateRequest rccUpdateRequest);
}
