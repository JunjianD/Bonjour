package com.djj.bj.ai.infrastructure.service;

import com.alibaba.fastjson2.JSON;
import com.djj.bj.ai.domain.chatgpt.model.request.AIRequest;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * AI 会话上下文缓存服务
 */
@Service
public class AIConversationContextService {
    private static final Logger logger = LoggerFactory.getLogger(AIConversationContextService.class);

    private static final String CONTEXT_KEY_PREFIX = "bj:ai:context:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${bj-ai.context.max-rounds:10}")
    private int maxRounds;

    @Value("${bj-ai.context.ttl-hours:24}")
    private long ttlHours;

    public List<AIRequest.Message> getHistory(String conversationId) {
        if (StringUtils.isBlank(conversationId)) {
            return Collections.emptyList();
        }
        List<String> history;
        try {
            history = stringRedisTemplate.opsForList().range(buildKey(conversationId), 0, -1);
        } catch (Exception e) {
            logger.warn("AIConversationContextService | 读取会话上下文失败，降级为无上下文模式 | conversationId: {}", conversationId, e);
            return Collections.emptyList();
        }
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }
        return history.stream()
                .map(item -> parseMessage(conversationId, item))
                .filter(Objects::nonNull)
                .toList();
    }

    public void appendConversation(String conversationId, AIRequest.Message userMessage, AIRequest.Message assistantMessage) {
        if (StringUtils.isBlank(conversationId) || userMessage == null || assistantMessage == null) {
            return;
        }
        try {
            String key = buildKey(conversationId);
            ListOperations<String, String> operations = stringRedisTemplate.opsForList();
            operations.rightPush(key, JSON.toJSONString(userMessage));
            operations.rightPush(key, JSON.toJSONString(assistantMessage));

            int maxMessageCount = Math.max(maxRounds, 1) * 2;
            Long size = operations.size(key);
            if (size != null && size > maxMessageCount) {
                operations.trim(key, size - maxMessageCount, size - 1);
            }
            stringRedisTemplate.expire(key, ttlHours, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.warn("AIConversationContextService | 写入会话上下文失败，跳过本次上下文缓存 | conversationId: {}", conversationId, e);
        }
    }

    private String buildKey(String conversationId) {
        return CONTEXT_KEY_PREFIX + conversationId;
    }

    private AIRequest.Message parseMessage(String conversationId, String item) {
        try {
            return JSON.parseObject(item, AIRequest.Message.class);
        } catch (Exception e) {
            logger.warn("AIConversationContextService | 解析会话上下文失败 | conversationId: {}", conversationId, e);
            return null;
        }
    }
}
