package com.djj.bj.platform.friend.application.event;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.friend.application.cache.FriendCacheService;
import com.djj.bj.platform.friend.domain.event.FriendEvent;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 基于RocketMQ的好友事件处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.event
 * @className FriendRocketMQEventHandler
 * @date 2025/8/2 16:41
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.EVENT_FRIEND_CONSUMER_GROUP, topic = PlatformConstants.TOPIC_EVENT_ROCKETMQ_FRIEND)
public class FriendRocketMQEventHandler implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(FriendRocketMQEventHandler.class);

    @Resource
    private FriendCacheService friendCacheService;

    @Override
    public void onMessage(String string) {
        logger.info("FriendRocketMQEventHandler | 接收到好友事件消息: {}", string);
        if (StrUtil.isBlank(string)) {
            logger.info("FriendRocketMQEventHandler | 接收好友事件参数错误");
            return;
        }
        FriendEvent event = this.getEventMessage(string);
        friendCacheService.updateFriendCache(event);
    }

    private FriendEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, FriendEvent.class);
    }
}
