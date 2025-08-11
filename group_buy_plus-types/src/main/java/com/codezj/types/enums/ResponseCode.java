package com.codezj.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    REPOSITORY_NOT_FOUND("BIZ_ERROR_0001", "数据库未查询到数据"),
    REPOSITORY_UPDATE_ZERO("BIZ_ERROR_0002", "数据库更新失败"),

    HTTP_EXCEPTION("HTTP_EXCEPTION", "HTTP 接口服务异常"),

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    E0001("E0001", "不存在对应的折扣计算服务"),
    E0002("E0002", "无营销配置"),
    E0003("E0003", "服务正在降级"),
    E0004("E0004", "服务正在切量"),
    E0005("E0005", "服务处于白名单模式。用户不在白名单中"),
    E0006("E0006", "拼团目标已完成"),
    E0007("E0007", "用户无拼团资格"),

    E0101("E0101", "该活动尚未生效"),
    E0102("E0102", "当前不在活动时间内"),
    E0103("E0103", "当前用户参加过太多次同一活动"),

    S0101("S0101", "结算规则过滤器-命中了SC黑名单"),
    S0102("S0102", "结算规则过滤器-不存在未支付的订单"),
    S0103("S0103", "结算规则过滤器-订单支付超时"),
    S0104("S0104", "结算规则过滤器-拼团不存在或已解散"),

    LOCK_ORDER_ERROR("LOCK_ORDER_ERROR", "锁单失败"),




    ;

    private String code;
    private String info;

}
