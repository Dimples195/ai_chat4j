package com.tp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

/**
 * chatGPT 相关配置
 */
@Data
@Repository
public class ChatConfig {
    /**
     * 代理 host
     */
    @Value("${chatgpt.proxy.host}")
    private String host;
    /**
     * 代理端口
     */
    @Value("${chatgpt.proxy.port}")
    private String port;
    /**
     * openAi key
     */
    @Value("${chatgpt.key}")
    private String key;
}
