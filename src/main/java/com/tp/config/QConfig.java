package com.tp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

/**
 * qq 配置
 */
@Data
@Repository
public class QConfig {
    /**
     * 是否启用
     */
    @Value("${qq.enable}")
    private Boolean enable;
    /**
     * 账号
     */
    @Value("${qq.account}")
    private Long account;
    /**
     * 密码
     */
    @Value("${qq.password}")
    private String password;
    /**
     * 登录方式
     */
    @Value("${qq.loginMethod}")
    private String loginMethod;
    /**
     * 是否同意添加新朋友
     */
    @Value("${qq.acceptNewFriend}")
    private Boolean acceptNewFriend;
    /**
     * 是否同意加入新群聊
     */
    @Value("${qq.acceptNewGroup}")
    private Boolean acceptNewGroup;
}
