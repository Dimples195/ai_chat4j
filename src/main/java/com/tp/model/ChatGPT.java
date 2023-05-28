package com.tp.model;

import cn.hutool.core.util.StrUtil;
import com.tp.config.ChatConfig;
import lombok.Data;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

@Data
public class ChatGPT {

    private String apiKey;

    private String model;

    private Messages messages;

    private Double temperature;

    private String n;

    private Proxy proxy;

    private String chatId;

    private String type;

    private ChatGPT() {
    }


    private ChatGPT(String api_key, String model, String chatId,Messages messages, Proxy proxy, Double temperature) {
        this.apiKey = api_key;
        this.model = model;
        this.chatId = chatId;
        this.messages = messages;
        this.proxy = proxy;
        this.temperature = temperature;
    }


    public static class ChatGPTBuilder {
        private String apiKey;

        private String model;

        private Messages messages;

        private Double temperature;

        private String n;

        private Proxy proxy;

        private String chatId;

        public ChatGPTBuilder() {
        }

        public ChatGPTBuilder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public ChatGPTBuilder setModel(String model) {
            this.model = model;
            return this;
        }

        public ChatGPTBuilder setMessages(Messages messages) {
            this.messages = messages;
            return this;
        }

        public ChatGPTBuilder setTemperature(Messages messages) {
            this.messages = messages;
            return this;
        }

        public ChatGPTBuilder setProxy(ChatConfig chatConfig) {
            if (Objects.nonNull(chatConfig)) {
                String host = chatConfig.getHost();
                String port = chatConfig.getPort();
                if (StrUtil.isNotBlank(host) && StrUtil.isNotBlank(port)) {
                    this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));
                }
            }
            return this;
        }

        public ChatGPTBuilder setProxy(String host, Integer port) {
            if (StrUtil.isNotBlank(host) && port != null) {
                this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            }
            return this;
        }

        public ChatGPTBuilder setChatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public ChatGPT build() {
            return new ChatGPT(apiKey, model, chatId,messages, proxy,temperature);
        }
    }
}
