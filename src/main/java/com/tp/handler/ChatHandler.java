package com.tp.handler;


import com.tp.model.ChatGPT;
import com.tp.model.Messages;
import com.tp.service.ChatService;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.tp.common.constant.ChatGPTConstant.ROLE_ASSISTANT;
import static com.tp.common.constant.ChatGPTConstant.ROLE_USER;

/**
 * 对话消息处理
 */
@Component
public class ChatHandler {
    /**
     * 询问
     *
     * @param event   mirai-qq消息事件
     * @param chatGPT gpt对象
     */
    public void ask(MessageEvent event, ChatGPT chatGPT) {

        chatGPT.setMessages(new Messages(ROLE_USER, cleanMsg(event)));

        CompletableFuture<String> future = ChatService.getInstance().askAsync(chatGPT, new ChatService.Callback<>() {
            @Override
            public void onResponse(String userId, String response) {
                Messages messages = new Messages(ROLE_ASSISTANT, response);
                ChatService.buildChatHistory4CallBack(userId, messages);
            }

            @Override
            public void onFailure(Throwable t) {
                // 处理错误
            }
        });
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

    /**
     * At消息提问时的消息处理
     * @param event 消息事件
     * @return 去除 @123*****789后的提问内容
     */
    private String cleanMsg(MessageEvent event) {
        String value = event.getMessage().contentToString().trim();
        // 去除 @123*****789 后提问
        if (value.contains("@")) {
            return value.replace("@" + event.getBot().getId(), "");
        }
        return value;
    }
}
