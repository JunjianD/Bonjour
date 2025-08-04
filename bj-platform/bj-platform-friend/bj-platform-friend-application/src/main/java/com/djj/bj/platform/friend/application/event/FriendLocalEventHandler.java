package com.djj.bj.platform.friend.application.event;

import com.alibaba.fastjson2.JSON;
import com.djj.bj.platform.friend.application.cache.FriendCacheService;
import com.djj.bj.platform.friend.domain.event.FriendEvent;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 基于spring的好友事件处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.event
 * @className FriendLocalEventHandler
 * @date 2025/8/2 16:41
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "local")
public class FriendLocalEventHandler {
    private final Logger logger = LoggerFactory.getLogger(FriendLocalEventHandler.class);

    @Resource
    private FriendCacheService friendCacheService;

    /**
     * 监听好友事件
     *
     * @param friendEvent 好友事件
     */
    @EventListener
    public void handleFriendEvent(FriendEvent friendEvent) {
        if (friendEvent == null || friendEvent.getEventId() == null) {
            logger.info("UserLocalEventHandler.handleFriendEvent|接收好友事件参数错误");
            return;
        }
        logger.info("UserLocalEventHandler.handleFriendEvent|接收到好友事件|{}", JSON.toJSON(friendEvent));
        // 更新好友缓存
        friendCacheService.updateFriendCache(friendEvent);
    }
}
