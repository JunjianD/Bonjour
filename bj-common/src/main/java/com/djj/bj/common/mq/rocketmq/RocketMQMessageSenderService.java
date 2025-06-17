package com.djj.bj.common.mq.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.BasicMessage;
import com.djj.bj.common.mq.MessageSenderService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * rocketMQ消息发送服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.mq.rocketmq
 * @className RocketMQMessageSenderService
 * @date 2025/6/10 22:03
 */
@Component
public class RocketMQMessageSenderService implements MessageSenderService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public boolean send(BasicMessage message) {
        try{
            SendResult sendResult = rocketMQTemplate.syncSend(message.getDestination(), this.getMessage(message));
            return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public TransactionSendResult sendMessageInTransaction(BasicMessage message, Object arg) {
        return rocketMQTemplate.sendMessageInTransaction(message.getDestination(), this.getMessage(message), arg);
    }

    private Message<String> getMessage(BasicMessage message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.MSG_KEY,message);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }
}
