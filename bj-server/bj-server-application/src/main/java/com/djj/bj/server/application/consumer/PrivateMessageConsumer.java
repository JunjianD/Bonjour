package com.djj.bj.server.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.djj.bj.server.application.netty.processor.MessageProcessor;
import com.djj.bj.server.application.netty.processor.factory.ProcessorFactory;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.model.ReceiveMessage;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 私聊消息消费者类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.consumer
 * @className PrivateMessageConsumer
 * @date 2025/7/7 17:26
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = Constants.MESSAGE_PRIVATE_CONSUMER_GROUP, topic = Constants.MESSAGE_GROUP_NULL_QUEUE)
public class PrivateMessageConsumer extends BasicMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageConsumer.class);

    @Value("${server.id}")
    private Long serverId;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)) {
            logger.warn("PrivateMessageConsumer.onMessage | 接收到的消息内容为空");
            return;
        }
        ReceiveMessage receiveMessage = this.getReceiveMessage(message);
        if (receiveMessage == null) {
            logger.warn("PrivateMessageConsumer.onMessage | 接收到的消息内容无法转换为ReceiveMessage对象");
            return;
        }
        MessageProcessor processor = ProcessorFactory.getProcessor(SystemInfoType.PRIVATE_CHAT);
        processor.process(receiveMessage);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        try {
            String topic = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_PRIVATE_QUEUE, String.valueOf(serverId));
            defaultMQPushConsumer.subscribe(topic, "*");
        } catch (Exception e) {
            logger.error("PrivateMessageConsumer.prepareStart | 发生异常: {}", e.getMessage());
        }
    }
}
