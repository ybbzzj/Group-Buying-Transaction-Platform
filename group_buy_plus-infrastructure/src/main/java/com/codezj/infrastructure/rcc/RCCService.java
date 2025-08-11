package com.codezj.infrastructure.rcc;

import com.codezj.types.annotation.RCC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: baozhongjie
 * @Version: v1.0.0
 * @Description: 动态配置中心服务
 **/
@Service
public class RCCService {

    /**
     * 降级开关
     * 0 关闭降级 default
     * 1 开启降级
     */
    @RCC("DOWNGRADE_SWITCH:0")
    private String DOWNGRADE_SWITCH;

    /**
     * 流量切分
     * 0 0%流量 default
     * 100 100%流量
     */
    @RCC("CUT_FLOW_RANGE:100")
    private String CUT_FLOW_RANGE;

    /**
     * 白名单模式
     */
    @RCC("WHITELIST_MODE_SWITCH:0")
    private String WHITELIST_MODE_SWITCH;

    @RCC("WHITELIST_USERS:{\"zhangsan\":\"\",\"lisi\":\"\"}")
    private Map<String, Object> WHITELIST_USERS;

    @RCC("SC_BLOCK_LIST:{\"s01\":[\"c02\"]}")
    private Map<String, List<String>> SC_BLOCK_LIST;

    /**
     * 是否降级
     */
    public boolean isDowngrade() {
        return "1".equals(DOWNGRADE_SWITCH);
    }

    /**
     * 是否在流量切分范围内
     */
    public boolean isInCutFlowRange(String userId) {
        // 取用户id哈希码最后两位与切流配置比较
        return Math.abs(userId.hashCode()) % 100 <= Integer.parseInt(CUT_FLOW_RANGE);
    }

    /**
     * 是否开启白名单模式
     */
    public boolean isWhiteListMode() {
        return "1".equals(WHITELIST_MODE_SWITCH);
    }

    /**
     * 是否在白名单中
     */
    public boolean isInWhiteList(String userId) {
        return WHITELIST_USERS.containsKey(userId);
    }

    /**
     * 是否在SC黑名单列表中
     */
    public boolean isInSCBlockList(String source, String channel) {
        return SC_BLOCK_LIST.containsKey(source) && SC_BLOCK_LIST.get(source).contains(channel);
    }
}
