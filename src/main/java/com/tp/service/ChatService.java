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

    private static final Map<String, List<Messages>> chatHistories = new ConcurrentHashMap<>();

    private ChatService() {
    }

    private static final class InstanceHolder {
        // 单例模式
        private static final ChatService instance = new ChatService();
    }

    public static ChatService getInstance() {
        return InstanceHolder.instance;
    }

    public static OkHttpClient getChatClient(ChatGPT chatGPT) {
        return new OkHttpClient.Builder()
                .proxy(chatGPT.getProxy())
                .readTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    private static Request buildChatRequest(ChatGPT chatGPT) {
        String model = chatGPT.getModel();
        Double temperature = chatGPT.getTemperature();
        Messages messages = chatGPT.getMessages();
        String n = chatGPT.getN();
        String type = chatGPT.getType();
        Gson gson = new Gson();

        Map<String, Object> requestBody = new HashMap<>();
        if (StrUtil.isNotBlank(model)) {
            requestBody.put("model", model);
        } else {
            model = "gpt-3.5-turbo";
            requestBody.put("model", model);
        }
        if (temperature != null) {
            requestBody.put("temperature", temperature);
        }
        if (StrUtil.isNotBlank(n)) {
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

    public static JsonArray buildChatHistory(String chatId, Messages messages) {
        List<Messages> chatHistory = chatHistories.computeIfAbsent(chatId, k -> new ArrayList<>());
        chatHistory.add(messages);
        chatHistories.put(chatId, chatHistory);
        return buildChatMessage(chatId);
    }

    public static void buildChatHistory4CallBack(String chatId, Messages messages) {
        List<Messages> chatHistory = chatHistories.computeIfAbsent(chatId, k -> new ArrayList<>());
        chatHistory.add(messages);
        chatHistories.put(chatId, chatHistory);
    }

    public static JsonArray buildChatMessage(String chatId) {
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();
        List<Messages> chatHistory = chatHistories.getOrDefault(chatId, new ArrayList<>());
        if (!chatHistory.isEmpty()) {
            jsonArray.addAll(gson.toJsonTree(chatHistory).getAsJsonArray());
        }
        return jsonArray;
    }

    public CompletableFuture<String> askAsync(ChatGPT chatGPT, Callback<String> callback) {
        OkHttpClient chatClient = getChatClient(chatGPT);
        Request request = buildChatRequest(chatGPT);
        CompletableFuture<String> future = new CompletableFuture<>();
        Gson gson = new Gson();
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

    public interface Callback<T> {
        void onResponse(String userId, T response);

        void onFailure(Throwable t);
    }

    private static String extractResponseText(JsonObject responseJson) {
        return responseJson.getAsJsonArray("choices").get(0)
                .getAsJsonObject().get("message")
                .getAsJsonObject().get("content")
                .toString()
                .replace("/n", "")
                .replace("\\","");
    }
}
