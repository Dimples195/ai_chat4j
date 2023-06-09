package com.tp.common.constant;

/**
 * 基本常量
 */
public class ChatGPTConstant {
    /**
     * API URL
     */
    public static final String URL_CHAT_COMPLETION = "https://api.openai.com/v1/chat/completions";
    /**
     * 单次问答类型
     */
    public static final String SINGLE_INTERACTION = "1";
    /**
     * 上下文对话
     */
    public static final String CONTINUOUS_INTERACTION = "2";
    /**
     * 角色类型-user
     */
    public static final String ROLE_USER = "user";
    /**
     * 角色类型-assistant
     */
    public static final String ROLE_ASSISTANT = "assistant";
}
