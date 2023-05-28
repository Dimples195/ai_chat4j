package com.tp.common.constant;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class ChatGPTConstant {
    public static final String URL_CHAT_COMPLETION = "https://api.openai.com/v1/chat/completions";
    /**
     * 单次问答类型
     */
    public static final String SINGLE_INTERACTION = "1";
    /**
     * 上下文对话
     */
    public static final String CONTINUOUS_INTERACTION = "2";

    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
}
