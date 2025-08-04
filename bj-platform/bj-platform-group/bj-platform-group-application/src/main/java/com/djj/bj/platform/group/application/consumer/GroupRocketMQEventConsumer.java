package com.djj.bj.platform.group.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.event.User2GroupEvent;
import com.djj.bj.platform.group.application.service.GroupService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 群组事件消费者
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.consumer
 * @className GroupRocketMQEventConsumer
 * @date 2025/8/5 01:18
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.TOPIC_USER_TO_GROUP_GROUP, topic = PlatformConstants.TOPIC_USER_TO_GROUP)
public class GroupRocketMQEventConsumer implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(GroupRocketMQEventConsumer.class);

    @Resource
    private GroupService groupService;

    @Override
    public void onMessage(String string) {
        if (StrUtil.isEmpty(string)) {
            logger.info("GroupRocketMQEventConsumer | 接收用户微服务发送过来的事件参数为空");
            return;
        }
        logger.info("GroupRocketMQEventConsumer | 接收用户微服务发送过来的事件参数: {}", string);
        User2GroupEvent user2GroupEvent = this.getEventMessage(string);
        groupService.updateHeadImgByUserId(user2GroupEvent.getHeadImageThumb(), user2GroupEvent.getEventId());
    }

    private User2GroupEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, User2GroupEvent.class);
    }
}
