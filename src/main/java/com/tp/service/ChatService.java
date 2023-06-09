package com.tp.service;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tp.model.ChatGPT;
import com.tp.model.Messages;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static com.tp.common.constant.ChatGPTConstant.CONTINUOUS_INTERACTION;
import static com.tp.common.constant.ChatGPTConstant.URL_CHAT_COMPLETION;


@Service
@Slf4j
public class ChatService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    /**
     * 对话历史
     */
    private static final Map<String, List<Messages>> chatHistories = new ConcurrentHashMap<>();

    private static final Gson gson = new Gson();

    private ChatService() {
    }

    private static final class InstanceHolder {
        // 单例模式
        private static final ChatService instance = new ChatService();
    }

    public static ChatService getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 创建 OkHttpClient
     * @param chatGPT chatGPT 对象
     * @return OkHttpClient
     */
    public static OkHttpClient getChatClient(ChatGPT chatGPT) {
        return new OkHttpClient.Builder()
                .proxy(chatGPT.getProxy())
                .readTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 构建询问请求
     * @param chatGPT chatGPT 对象
     * @return Request请求
     */
    private static Request buildChatRequest(ChatGPT chatGPT) {
        String model = chatGPT.getModel();
        Double temperature = chatGPT.getTemperature();
        Messages messages = chatGPT.getMessages();
        Integer n = chatGPT.getN();
        String type = chatGPT.getType();

        Map<String, Object> requestBody = new HashMap<>();
        if (StrUtil.isNotBlank(model)) {
            requestBody.put("model", model);
        }
        if (temperature != null) {
            requestBody.put("temperature", temperature);
        }
        if (n != null) {
            requestBody.put("n", n);
        }
        if (StrUtil.isNotBlank(messages.getContent())) {
            requestBody.put("messages",
                    CONTINUOUS_INTERACTION.equals(type) ? buildChatHistory(chatGPT.getChatId(), messages) : new Messages[]{messages});
        } else {
            throw new RuntimeException("request message is null");
        }
        return new Request.Builder()
                .url(URL_CHAT_COMPLETION)
                .header("Authorization", "Bearer " + chatGPT.getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json; charset=utf-8")))
                .build();
    }

    /**
     * 根据chatId构建专属历史消息
     * @param chatId chatGPT编号
     * @param messages 消息数据
     * @return 消息体
     */
    public static JsonArray buildChatHistory(String chatId, Messages messages) {
        List<Messages> chatHistory = chatHistories.computeIfAbsent(chatId, k -> new ArrayList<>());
        chatHistory.add(messages);
        chatHistories.put(chatId, chatHistory);
        return buildChatMessage(chatId);
    }

    /**
     * 当对话模式为连续时，调用回调函数，实现历史对话数据的存储
     * @param chatId chatGPT的编号
     * @param messages 消息数据
     */
    public static void buildChatHistory4CallBack(String chatId, Messages messages) {
        List<Messages> chatHistory = chatHistories.computeIfAbsent(chatId, k -> new ArrayList<>());
        chatHistory.add(messages);
        chatHistories.put(chatId, chatHistory);
    }

    /**
     * 构建消息
     * @param chatId chatGPT编号
     * @return 消息体
     */
    public static JsonArray buildChatMessage(String chatId) {
        JsonArray jsonArray = new JsonArray();
        List<Messages> chatHistory = chatHistories.getOrDefault(chatId, new ArrayList<>());
        if (!chatHistory.isEmpty()) {
            jsonArray.addAll(gson.toJsonTree(chatHistory).getAsJsonArray());
        }
        return jsonArray;
    }

    /**
     * 提问获取结果的主要逻辑
     * @param chatGPT chatGPT 对象
     * @param callback 回调函数
     * @return 异步消息获取对象
     */
    public CompletableFuture<String> askAsync(ChatGPT chatGPT, Callback<String> callback) {
        OkHttpClient chatClient = getChatClient(chatGPT);
        Request request = buildChatRequest(chatGPT);
        CompletableFuture<String> future = new CompletableFuture<>();
        executor.submit(() -> {
            try (Response response = chatClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException(response.toString());
                }
                if (response.body() != null) {

                    String tempStr = response.body().string();
                    JsonObject jsonObject = gson.fromJson(tempStr, JsonObject.class);
                    String result = extractResponseText(jsonObject);
                    future.complete(result);
                    if (CONTINUOUS_INTERACTION.equals(chatGPT.getType())) {
                        callback.onResponse(chatGPT.getChatId(), result);
                    }
                }
            } catch (IOException e) {
                future.completeExceptionally(e);
                callback.onFailure(e);
            }
        });
        return future;
    }

    /**
     * 回调函数接口
     * @param <T>
     */
    public interface Callback<T> {
        void onResponse(String userId, T response);

        void onFailure(Throwable t);
    }

    /**
     * 处理响应结果
     * @param responseJson 响应文本
     * @return 去除 /n 、 \  符号
     */
    private static String extractResponseText(JsonObject responseJson) {
        return responseJson.getAsJsonArray("choices").get(0)
                .getAsJsonObject().get("message")
                .getAsJsonObject().get("content")
                .toString()
                .replace("/n", "")
                .replace("\\","");
    }
}
