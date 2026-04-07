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
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

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

    @Value("${bj-ai.chatgpt.key}")
    private String aiKey;

    @Value("${bj-ai.chatgpt.api}")
    private String api;

    @Override
    public String sendMessage(String requestData) throws IOException {
        if (StringUtils.isEmpty(requestData)) {
            throw new AIException(HttpCode.PARAMS_ERROR);
        }

        try (CloseableHttpClient httpClient = HttpClient.createSSLClientDefault()) {

            HttpPost httpPost = new HttpPost(api);
            httpPost.addHeader(AIConstants.CONTENT_TYPE, AIConstants.APPLICATION_JSON);
            httpPost.addHeader(AIConstants.AUTHORIZATION, AIConstants.BEARER.concat(aiKey));

            AIRequest request = new AIRequest("user",requestData);

            StringEntity entity = new StringEntity(
                    JSON.toJSONString(request),
                    ContentType.create(AIConstants.TEXT_JSON, AIConstants.CHARSET_UTF_8)
            );
            httpPost.setEntity(entity);

            return httpClient.execute(httpPost, response -> {
                if (response.getCode() == HttpStatus.SC_OK) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    AIMessage aiMessage = JSON.parseObject(responseStr, AIMessage.class);
                    List<ChoiceInfo> choices = aiMessage.getChoices();

                    StringBuilder resultBuilder = new StringBuilder();
                    for (ChoiceInfo choice : choices) {
//                        resultBuilder.append(choice.getText());
                        if(choice.getMessage() != null){
                            resultBuilder.append(choice.getMessage().getContent());
                        }
                    }
                    return resultBuilder.toString();
                } else {
//                    throw new AIException(
//                            "调用ChatGPT接口异常, 返回的状态码为: " + response.getCode()
//                    );
                    logger.error("调用ChatGPT接口异常, 返回的状态码为: {}" , response.getCode());
                }
                return AIConstants.DEFAULT_MESSAGE;
            });
        }
    }
}
