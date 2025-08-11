package com.codezj.infrastructure.gateway;

import com.codezj.types.enums.ResponseCode;
import com.codezj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: TODO
 **/
@Slf4j
@Service
public class GroupBuyNotifyService {
    @Resource
    private OkHttpClient okHttpClient;

    public String groupBuyNotify(String apiUrl, String notifyRequestDTOJSON) throws Exception {
        try {
            // 1. 构建参数
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(notifyRequestDTOJSON, mediaType);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .build();

            // 2. 调用接口
            Response response = okHttpClient.newCall(request).execute();

            log.info("[HTTP接口服务] 拼团回调, apiUrl:{}, notifyRequestDTOJSON:{}, response:{}", apiUrl, notifyRequestDTOJSON, response);
            // 3. 返回结果
            return Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            log.error("拼团回调 HTTP 接口服务异常 {}", apiUrl, e);
            throw new AppException(ResponseCode.HTTP_EXCEPTION.getCode(), ResponseCode.HTTP_EXCEPTION.getInfo());
        }
    }
}
