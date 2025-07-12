package com.djj.bj.sdk.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.sdk.infrastructure.multicaster.MessageListenerMulticaster;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 群聊消息结果消费者类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.application.consumer
 * @className GroupMessageResultConsumer
 * @date 2025/7/12 18:50
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = Constants.RESULT_MESSAGE_GROUP_CONSUMER_GROUP,topic = Constants.RESULT_MESSAGE_GROUP_QUEUE)
public class GroupMessageResultConsumer extends BaseMessageResultConsumer implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageResultConsumer.class);

    @Resource
    private MessageListenerMulticaster messageListenerMulticaster;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)) {
            logger.warn("GroupMessageResultConsumer.onMessage | 接收到的消息内容为空");
            return;
        }
        SendResult<?> sendResult = this.getResultMessage(message);
        if(sendResult == null) {
            logger.warn("GroupMessageResultConsumer.onMessage | 接收到的消息内容转换为SendResult对象后为空");
            return;
        }
        messageListenerMulticaster.multicast(ListeningType.GROUP_MESSAGE, sendResult);
    }
}
