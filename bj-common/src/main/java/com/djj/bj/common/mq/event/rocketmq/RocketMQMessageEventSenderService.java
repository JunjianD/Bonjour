package com.djj.bj.common.mq.event.rocketmq;

import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.BasicMessage;
import com.djj.bj.common.mq.event.MessageEventSenderService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * RocketMQ消息事件发送服务
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.mq.event.rocketmq
 * @className RocketMQMessageEventSenderService
 * @date 2025/7/25 16:50
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "rocketmq")
public class RocketMQMessageEventSenderService implements MessageEventSenderService {
    private final Logger logger = LoggerFactory.getLogger(RocketMQMessageEventSenderService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public boolean send(BasicMessage message) {
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(message.getDestination(), this.getMessage(message));
            return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
        } catch (Exception e) {
            logger.error("RocketMQMessageEventSenderService.send | 发送消息失败, message: {}\n, error: {}", message, e.getMessage());
            return false;
        }
    }

    private Message<String> getMessage(BasicMessage message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.MSG_KEY, message);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }

    @Override
    public TransactionSendResult sendMessageInTransaction(BasicMessage message, Object arg) {
        return rocketMQTemplate.sendMessageInTransaction(message.getDestination(), this.getMessage(message), arg);
    }
}
