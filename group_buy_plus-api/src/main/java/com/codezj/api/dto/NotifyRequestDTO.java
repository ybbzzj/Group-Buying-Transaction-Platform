package com.codezj.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: 1.0.0
 * @Description: TODO
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyRequestDTO {
    private String teamId;

    private String parameterJson;
}
