package com.codezj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 用户实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    /** 用户ID */
    private String userId;

}
