package com.tp.handler;

import com.tp.factory.ChatFactory;
import com.tp.model.ChatGPT;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.tp.common.constant.ChatGPTConstant.CONTINUOUS_INTERACTION;
import static com.tp.common.constant.ChatGPTConstant.SINGLE_INTERACTION;


@Component
public class QHandler implements ListenerHost {

    @Autowired
    ChatHandler chatHandler;
    @Autowired
    ChatFactory chatFactory;

    /**
     * 好友消息事件
     *
     * @param event 事件
     */
    @EventHandler
    public void onFriendMessageEvent(FriendMessageEvent event) {
        String id = String.valueOf(event.getSubject().getId());
        ChatGPT chatGPT = chatFactory.getChat(id);
        chatGPT.setType(CONTINUOUS_INTERACTION);
        chatHandler.ask(event, chatGPT);
    }

    /**
     * 群聊消息事件
     *
     * @param event 事件
     */
    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        if (event.getMessage().contains(new At(event.getBot().getId()))) {
            String id = String.valueOf(event.getSubject().getId());
            ChatGPT chatGPT = chatFactory.getChat(id);
            chatGPT.setType(SINGLE_INTERACTION);
            chatHandler.ask(event, chatGPT);
        }
    }


}
