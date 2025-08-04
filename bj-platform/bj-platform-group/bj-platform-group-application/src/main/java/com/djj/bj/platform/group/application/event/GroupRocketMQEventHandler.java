package com.djj.bj.platform.group.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.group.application.cache.GroupCacheService;
import com.djj.bj.platform.group.domain.event.GroupEvent;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 群组事件处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.event
 * @className GroupRocketMQEventHandler
 * @date 2025/8/5 01:04
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.EVENT_GROUP_CONSUMER_GROUP, topic = PlatformConstants.TOPIC_EVENT_ROCKETMQ_GROUP)
public class GroupRocketMQEventHandler implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(GroupRocketMQEventHandler.class);

    @Resource
    private GroupCacheService groupCacheService;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isBlank(message)) {
            logger.info("GroupRocketMQEventHandler | 接收群组事件参数错误");
            return;
        }
        logger.info("GroupRocketMQEventHandler | 接收群组事件参数: {}", message);
        GroupEvent groupEvent = this.getEventMessage(message);
        groupCacheService.updateGroupCache(groupEvent);
    }

    private GroupEvent getEventMessage(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, GroupEvent.class);
    }
}
