package com.tp.handler;


import com.tp.config.ChatConfig;
import com.tp.model.ChatGPT;
import com.tp.model.Messages;
import com.tp.service.ChatService;
import net.mamoe.mirai.contact.MessageTooLargeException;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.tp.common.constant.ChatGPTConstant.ROLE_ASSISTANT;
import static com.tp.common.constant.ChatGPTConstant.ROLE_USER;


@Component
public class ChatHandler {

    public void ask(MessageEvent event, ChatGPT chatGPT) {

        String value = event.getMessage().contentToString().trim();
        if (value.contains("@")) {
            value = value.replace("@" + event.getBot().getId(), "");
        }
        chatGPT.setMessages(new Messages(ROLE_USER, value));

        ChatService.Callback<String> callback = new ChatService.Callback<>() {

            @Override
            public void onResponse(String userId, String response) {
                Messages messages = new Messages(ROLE_ASSISTANT, response);
                ChatService.buildChatHistory4CallBack(userId, messages);
            }

            @Override
            public void onFailure(Throwable t) {
                // 处理错误
            }
        };
        CompletableFuture<String> future = ChatService.getInstance().askAsync(chatGPT, callback);
        try {
            MessageChain messages = new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(future.get())
                    .build();
            event.getSubject().sendMessage(messages);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
