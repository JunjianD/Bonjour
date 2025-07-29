package com.djj.bj.platform.user.application.event;

import com.alibaba.fastjson2.JSON;
import com.djj.bj.platform.user.application.cache.service.UserCacheService;
import com.djj.bj.platform.user.domain.event.UserEvent;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 本地用户事件处理器，基于spring
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.event
 * @className UserLocalEventHandler
 * @date 2025/7/25 18:32
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "local")
public class UserLocalEventHandler {
    private final Logger logger = LoggerFactory.getLogger(UserLocalEventHandler.class);

    @Resource
    private UserCacheService userCacheService;

    /**
     * 监听用户事件
     *
     * @param userEvent 用户事件
     */
    @EventListener
    public void handleUserEvent(UserEvent userEvent) {
        if (userEvent == null || userEvent.getEventId() == null) {
            logger.info("UserLocalEventHandler.handleUserEvent|接收用户事件参数错误");
            return;
        }
        logger.info("UserLocalEventHandler.handleUserEvent|接收到用户事件|{}", JSON.toJSON(userEvent));
        // 更新用户缓存
        userCacheService.updateUserCache(userEvent.getEventId());
    }
}
