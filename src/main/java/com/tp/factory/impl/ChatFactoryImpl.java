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

    @Override
    public ChatGPT createChat(String id) {
        return new ChatGPT.ChatGPTBuilder()
                .setChatId(id)
                .setProxy(chatConfig)
                .setApiKey(chatConfig.getOpen_ai_key())
                .build();
    }

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
