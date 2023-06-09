package com.tp.factory.impl;

import com.tp.config.ChatConfig;
import com.tp.factory.ChatFactory;
import com.tp.model.ChatGPT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ChatFactoryImpl implements ChatFactory {
    private final Map<String, ChatGPT> cacheMap = new HashMap<>();
    @Autowired
    ChatConfig chatConfig;

    /**
     * 创建Chat对象
     *
     * @param id 每一个对象的唯一标识
     * @return Chat对象
     */
    @Override
    public ChatGPT createChat(String id) {
        return new ChatGPT.ChatGPTBuilder()
                .setChatId(id)
                .setProxy(chatConfig)
                .setApiKey(chatConfig.getKey())
                .build();
    }

    /**
     * 实现Chat对象复用
     *
     * @param id 每一个对象的唯一标识
     * @return Chat对象
     */
    @Override
    public ChatGPT getChat(String id) {
        ChatGPT chatGPT = cacheMap.get(id);
        if (Objects.isNull(chatGPT)) {
            ChatGPT chat = createChat(id);
            cacheMap.put(id, chat);
            return chat;
        } else {
            return chatGPT;
        }
    }
}
