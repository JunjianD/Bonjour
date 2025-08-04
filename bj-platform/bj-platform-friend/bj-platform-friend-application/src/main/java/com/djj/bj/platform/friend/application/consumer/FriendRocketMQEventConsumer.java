package com.djj.bj.platform.friend.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.event.User2FriendEvent;
import com.djj.bj.platform.friend.application.service.FriendService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 消费信息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.consumer
 * @className FriendRocketMQEventConsumer
 * @date 2025/8/2 17:32
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.TOPIC_USER_TO_FRIEND_GROUP, topic = PlatformConstants.TOPIC_USER_TO_FRIEND)
public class FriendRocketMQEventConsumer implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(FriendRocketMQEventConsumer.class);

    @Resource
    private FriendService friendService;

    @Override
    public void onMessage(String string) {
        if (StrUtil.isEmpty(string)) {
            logger.info("FriendRocketMQEventConsumer | 接收用户微服务发送过来的事件参数为空");
            return;
        }
        logger.info("FriendRocketMQEventConsumer | 接收用户微服务发送过来的事件参数: {}", string);
        User2FriendEvent user2FriendEvent = this.getEventMessage(string);
        friendService.updateFriendByFriendId(user2FriendEvent.getHeadImg(), user2FriendEvent.getNickName(), user2FriendEvent.getEventId());
    }

    private User2FriendEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, User2FriendEvent.class);
    }
}
