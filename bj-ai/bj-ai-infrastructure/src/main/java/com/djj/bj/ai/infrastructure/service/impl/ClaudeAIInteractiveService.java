package com.djj.bj.ai.infrastructure.service.impl;

import com.djj.bj.ai.common.constants.AIConstants;
import com.djj.bj.ai.domain.service.AIInteractiveService;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * AIInteractiveService接口的实现类，负责与Claude进行交互
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.infrastructure.service.impl
 * @className ClaudeAIInteractiveService
 * @date 2026/3/5 23:41
 */
public class ClaudeAIInteractiveService implements AIInteractiveService {
    @Value("${bj-ai.claude.key}")
    private String aiKey;

    @Value("${bj-ai.claude.api}")
    private String api;

    @Override
    public String sendMessage(String requestData) throws IOException {
        return AIConstants.DEFAULT_MESSAGE;
    }
}
