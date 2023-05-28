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
@PropertySource("classpath:application.yml")
public class ChatConfig {
    @Value("${chatgpt.proxy.host}")
    private String host;
    @Value("${chatgpt.proxy.port}")
    private String port;
    @Value("${chatgpt.open_ai_key}")
    private String open_ai_key;
}
