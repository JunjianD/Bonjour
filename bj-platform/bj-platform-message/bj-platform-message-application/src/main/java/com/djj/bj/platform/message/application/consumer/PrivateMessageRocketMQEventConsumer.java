package com.djj.bj.platform.message.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.PrivateChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 私聊消息RocketMQ事件消费者
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.consumer
 * @className PrivateMessageRocketMQEventConsumer
 * @date 2025/8/5 14:49
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.TOPIC_PRIVATE_TX_MESSAGE_GROUP, topic = PlatformConstants.TOPIC_PRIVATE_TX_MESSAGE)
public class PrivateMessageRocketMQEventConsumer implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageRocketMQEventConsumer.class);

    @Resource
    private Client client;

    @Override
    public void onMessage(String string) {
        if (StrUtil.isEmpty(string)) {
            logger.info("PrivateMessageRocketMQEventConsumer | 接收消息微服务发送过来的私聊消息事件参数为空");
            return;
        }
        logger.info("PrivateMessageRocketMQEventConsumer | 接收消息微服务发送过来的私聊消息事件: {}", string);
        PrivateMessageTxEvent privateMessageTxEvent = this.getEventMessage(string);
        if (privateMessageTxEvent == null || privateMessageTxEvent.getPrivateMessageDTO() == null) {
            logger.error("PrivateMessageRocketMQEventConsumer | 接收消息微服务发送过来的私聊消息事件解析失败");
            return;
        }
        PrivateMessageVO privateMessageVO = BeanUtils.copyProperties(privateMessageTxEvent.getPrivateMessageDTO(), PrivateMessageVO.class);
        // 设置消息id
        privateMessageVO.setId(privateMessageTxEvent.getEventId());
        // 设置发送者id
        privateMessageVO.setSendId(privateMessageTxEvent.getSendId());
        // 设置发送状态
        privateMessageVO.setStatus(MessageStatus.UNSEND.getCode());
        // 设置发送时间
        privateMessageVO.setSendTime(privateMessageTxEvent.getSendTime());
        // 封装
        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(privateMessageVO.getSendId(), privateMessageTxEvent.getTerminal()));
        sendMessage.setReceiverId(privateMessageVO.getRecvId());
        sendMessage.setSendToSelfOtherTerminals(true);
        sendMessage.setContent(privateMessageVO);
        client.sendPrivateMessage(sendMessage);
        logger.info(
                "PrivateMessageRocketMQEventConsumer | 发送私聊消息，发送者id: {}, 接收者id: {}, 内容: {}",
                privateMessageVO.getSendId(),
                privateMessageVO.getRecvId(),
                privateMessageVO.getContent()
        );
    }

    private PrivateMessageTxEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, PrivateMessageTxEvent.class);
    }

}
