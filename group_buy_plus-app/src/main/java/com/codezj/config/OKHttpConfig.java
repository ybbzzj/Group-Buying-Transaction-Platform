package com.codezj.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: okhttp配置
 **/
@Configuration
public class OKHttpConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}
