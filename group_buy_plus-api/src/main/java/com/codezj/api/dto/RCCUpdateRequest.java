package com.codezj.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: RCC更新请求
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RCCUpdateRequest {
    private String rccKey;
    private String rccValue;
}
