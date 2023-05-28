package com.tp.factory;

import com.tp.model.ChatGPT;

public interface ChatFactory {
    ChatGPT createChat(String id);

    ChatGPT getChat(String id);
}
