package com.djj.bj.platform.user.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.user.application.cache.service.UserCacheService;
import com.djj.bj.platform.user.domain.event.UserEvent;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 基于rocketmq的事件处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.event
 * @className UserRocketMQEventHandler
 * @date 2025/7/25 18:33
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.EVENT_USER_CONSUMER_GROUP, topic = PlatformConstants.TOPIC_EVENT_ROCKETMQ_USER)
public class UserRocketMQEventHandler implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(UserRocketMQEventHandler.class);

    @Resource
    private UserCacheService userCacheService;

    @Override
    public void onMessage(String string) {
        logger.info("UserRocketMQEventHandler.onMessage|接收到用户事件: {}", string);
        if (StrUtil.isEmpty(string)) {
            logger.warn("UserRocketMQEventHandler.onMessage|接收用户事件参数错误");
            return;
        }
        UserEvent userEvent = this.getEventMessage(string);
        userCacheService.updateUserCache(userEvent.getEventId());
    }

    private UserEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, UserEvent.class);
    }
}
