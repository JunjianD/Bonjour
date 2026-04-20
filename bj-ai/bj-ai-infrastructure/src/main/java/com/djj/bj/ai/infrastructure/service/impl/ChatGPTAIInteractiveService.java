package com.djj.bj.ai.infrastructure.service.impl;

import com.alibaba.fastjson2.JSON;
import com.djj.bj.ai.common.constants.AIConstants;
import com.djj.bj.ai.common.enums.HttpCode;
import com.djj.bj.ai.common.exception.AIException;
import com.djj.bj.ai.common.httpclient.HttpClient;
import com.djj.bj.ai.domain.chatgpt.model.message.AIMessage;
import com.djj.bj.ai.domain.chatgpt.model.request.AIRequest;
import com.djj.bj.ai.domain.chatgpt.model.vo.ChoiceInfo;
import com.djj.bj.ai.domain.service.AIInteractiveService;
import com.djj.bj.ai.infrastructure.service.AIConversationContextService;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

/**
 * AIInteractiveService接口的实现类，负责与ChatGPT进行交互
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.infrastructure.service.impl
 * @className ChatGPTAIInteractiveService
 * @date 2026/3/4 23:54
 */
@Service
@ConditionalOnProperty(name = "bj-ai.type", havingValue = "chatgpt")
public class ChatGPTAIInteractiveService implements AIInteractiveService {
    private final Logger logger = LoggerFactory.getLogger(ChatGPTAIInteractiveService.class);

    @Resource
    private AIConversationContextService aiConversationContextService;

    @Value("${bj-ai.chatgpt.key}")
    private String aiKey;

    @Value("${bj-ai.chatgpt.api}")
    private String api;

    @Override
    public String sendMessage(String requestData) throws IOException {
        return sendMessage(null, null, null, requestData);
    }

    @Override
    public String sendMessage(String conversationId, Long userId, String userName, String requestData) throws IOException {
        if (StringUtils.isEmpty(requestData)) {
            throw new AIException(HttpCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyEmpty(aiKey, api)) {
            throw new AIException("AI接口未配置，请检查 bj-ai.chatgpt.key 和 bj-ai.chatgpt.api");
        }

        try (CloseableHttpClient httpClient = HttpClient.createSSLClientDefault()) {

            HttpPost httpPost = new HttpPost(api);
            httpPost.addHeader(AIConstants.CONTENT_TYPE, AIConstants.APPLICATION_JSON);
            httpPost.addHeader(AIConstants.AUTHORIZATION, AIConstants.BEARER.concat(aiKey));

            AIRequest.Message userMessage = new AIRequest.Message("user", requestData);
            AIRequest request = new AIRequest();
            request.setMessages(buildMessages(conversationId, userId, userName, userMessage));

            StringEntity entity = new StringEntity(
                    JSON.toJSONString(request),
                    ContentType.create(AIConstants.TEXT_JSON, AIConstants.CHARSET_UTF_8)
            );
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getCode() == HttpStatus.SC_OK) {
                    String responseStr;
                    try {
                        responseStr = EntityUtils.toString(response.getEntity());
                    } catch (ParseException e) {
                        throw new AIException("解析AI响应失败");
                    }
                    AIMessage aiMessage = JSON.parseObject(responseStr, AIMessage.class);
                    List<ChoiceInfo> choices = aiMessage.getChoices();

                    StringBuilder resultBuilder = new StringBuilder();
                    for (ChoiceInfo choice : choices) {
                        if (choice.getMessage() != null) {
                            resultBuilder.append(choice.getMessage().getContent());
                        }
                    }
                    String reply = StringUtils.defaultIfBlank(resultBuilder.toString(), AIConstants.DEFAULT_MESSAGE);
                    cacheConversation(conversationId, userMessage, reply);
                    return reply;
                }
                logger.error("调用ChatGPT接口异常, 返回的状态码为: {}", response.getCode());
                return AIConstants.DEFAULT_MESSAGE;
            }
        }
    }

    private List<AIRequest.Message> buildMessages(String conversationId, Long userId, String userName, AIRequest.Message userMessage) {
        List<AIRequest.Message> messages = new ArrayList<>();
        messages.add(new AIRequest.Message("system", buildSystemPrompt(conversationId, userId, userName)));
        if (StringUtils.isNotBlank(conversationId)) {
            messages.addAll(aiConversationContextService.getHistory(conversationId));
        }
        messages.add(userMessage);
        return messages;
    }

    private void cacheConversation(String conversationId, AIRequest.Message userMessage, String reply) {
        if (StringUtils.isBlank(conversationId) || StringUtils.isBlank(reply)) {
            return;
        }
        aiConversationContextService.appendConversation(
                conversationId,
                userMessage,
                new AIRequest.Message("assistant", reply)
        );
    }

    private String buildSystemPrompt(String conversationId, Long userId, String userName) {
        StringBuilder prompt = new StringBuilder("你是Bonjour即时通讯系统中的AI助手，请使用自然、简洁、友好的中文回答用户问题。");
        if (userId != null) {
            prompt.append(" 当前与你对话的用户ID为 ").append(userId).append("。");
        }
        if (StringUtils.isNotBlank(userName)) {
            prompt.append(" 当前用户昵称为 ").append(userName).append("。");
        }
        if (StringUtils.isNotBlank(conversationId)) {
            prompt.append(" 当前会话标识为 ").append(conversationId).append("，请仅基于该会话的上下文连续回答，不要混淆其他用户或其他会话。");
        }
        return prompt.toString();
    }
}
