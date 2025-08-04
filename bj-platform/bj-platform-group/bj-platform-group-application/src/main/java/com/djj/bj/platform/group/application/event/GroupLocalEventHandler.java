package com.djj.bj.platform.group.application.event;

import com.djj.bj.platform.group.application.cache.GroupCacheService;
import com.djj.bj.platform.group.domain.event.GroupEvent;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 群组本地事件处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.event
 * @className GroupLocalEventHandler
 * @date 2025/8/5 01:00
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "local")
public class GroupLocalEventHandler {
    private final Logger logger = LoggerFactory.getLogger(GroupLocalEventHandler.class);

    @Resource
    private GroupCacheService groupCacheService;

    @EventListener
    public void handleGroupEvent(GroupEvent groupEvent) {
        if (groupEvent == null || groupEvent.getEventId() == null) {
            logger.info("GroupLocalEventHandler.handleGroupEvent | 接收群组事件参数错误");
            return;
        }
        logger.info("GroupLocalEventHandler.handleGroupEvent | 接收到群组事件 | {}", groupEvent);
        // 更新群组缓存
        groupCacheService.updateGroupCache(groupEvent);
    }
}
