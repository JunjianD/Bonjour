package com.djj.bj.platform.message.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.ai.dubbo.service.AIDubboService;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.PrivateChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.common.threadpool.PrivateMessageThreadPoolUtils;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

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

    @Value("${bj.ai.userid:10000000001}")
    private Long aiUserId;

    @Value("${bj.ai.username:binghe}")
    private String aiUserName;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false, timeout = 60000, retries = 0)
    private AIDubboService aiDubboService;

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
        // 向指定用户发送消息，触发大模型流程
        if (PlatformConstants.AI_USER_ID.equals(privateMessageVO.getRecvId())) {
            PrivateMessageThreadPoolUtils.execute(() -> {
                this.sendAiMessage(privateMessageVO, privateMessageTxEvent.getTerminal());
            });
        }
    }

    /**
     * 发送Ai大模型消息
     */
    private void sendAiMessage(PrivateMessageVO privateMessageVO, Integer terminal) {
        // 不是对接AI的账号
        Long recvId = privateMessageVO.getRecvId();
        if (!PlatformConstants.AI_USER_ID.equals(recvId)) {
            return;
        }
        try {
            logger.info("私聊发送AI消息开始");
            String aiMessage = aiDubboService.sendMessage(privateMessageVO.getContent());
            logger.info("私聊发送AI消息，AI返回的消息内容: {}", aiMessage);

            PrivateMessageVO aiMessageVO = new PrivateMessageVO();

            aiMessageVO.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
            aiMessageVO.setSendId(aiUserId);
            aiMessageVO.setRecvId(privateMessageVO.getSendId());
            aiMessageVO.setContent(aiMessage);
            aiMessageVO.setType(privateMessageVO.getType());
            aiMessageVO.setStatus(MessageStatus.UNSEND.getCode());
            aiMessageVO.setSendTime(new Date());

            PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
            sendMessage.setSender(new UserInfo(aiMessageVO.getSendId(), terminal));
            sendMessage.setReceiverId(aiMessageVO.getRecvId());
            sendMessage.setSendToSelfOtherTerminals(true);
            sendMessage.setContent(aiMessageVO);
            client.sendPrivateMessage(sendMessage);
            logger.info("私聊发送AI消息结束");
        } catch (IOException e) {
            logger.error("对接AI大模型消息异常: ", e);
        }
    }

    private PrivateMessageTxEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, PrivateMessageTxEvent.class);
    }

}
