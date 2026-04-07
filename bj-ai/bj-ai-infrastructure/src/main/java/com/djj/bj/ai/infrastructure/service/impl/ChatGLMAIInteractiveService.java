package com.djj.bj.ai.infrastructure.service.impl;

import com.djj.bj.ai.common.constants.AIConstants;
import com.djj.bj.ai.domain.service.AIInteractiveService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * AIInteractiveService接口的实现类，负责与ChatGLM进行交互
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.infrastructure.service.impl
 * @className ChatGLMAIInteractiveService
 * @date 2026/3/5 23:39
 */
@Service
@ConditionalOnProperty(name = "bj-ai.type", havingValue = "chatglm")
public class ChatGLMAIInteractiveService implements AIInteractiveService {

    @Value("${bj-ai.chatglm.key}")
    private String aiKey;

    @Value("${bj-ai.chatglm.api}")
    private String api;

    @Override
    public String sendMessage(String requestData) throws IOException {
        return AIConstants.DEFAULT_MESSAGE;
    }
}
