package com.djj.bj.platform.message.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.GroupChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 群聊消息RocketMQ事件消费者
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.consumer
 * @className GroupMessageRocketMQEventConsumer
 * @date 2025/8/7 10:09
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = PlatformConstants.TOPIC_GROUP_TX_MESSAGE_GROUP, topic = PlatformConstants.TOPIC_GROUP_TX_MESSAGE)
public class GroupMessageRocketMQEventConsumer implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageRocketMQEventConsumer.class);

    @Resource
    private Client client;

    @Override
    public void onMessage(String string) {
        if (StrUtil.isEmpty(string)) {
            logger.info("GroupMessageRocketMQEventConsumer | 接收消息微服务发送过来的群聊消息事件参数为空");
            return;
        }
        logger.info("GroupMessageRocketMQEventConsumer | 接收消息微服务发送过来的群聊消息事件: {}", string);
        GroupMessageTxEvent groupMessageTxEvent = this.getEventMessage(string);
        if (groupMessageTxEvent == null || groupMessageTxEvent.getGroupMessageDTO() == null) {
            logger.error("GroupMessageRocketMQEventConsumer | 接收消息微服务发送过来的群聊消息事件解析失败");
            return;
        }
        GroupMessageVO groupMessageVO = BeanUtils.copyProperties(groupMessageTxEvent.getGroupMessageDTO(), GroupMessageVO.class);
        groupMessageVO.setId(groupMessageTxEvent.getEventId());
        groupMessageVO.setSendId(groupMessageTxEvent.getSendId());
        groupMessageVO.setSendNickName(groupMessageTxEvent.getSendNickName());
        groupMessageVO.setSendTime(groupMessageTxEvent.getSendTime());
        groupMessageVO.setStatus(MessageStatus.UNSEND.getCode());

        GroupChat<GroupMessageVO> sendMessage = new GroupChat<>();
        sendMessage.setSender(new UserInfo(groupMessageTxEvent.getSendId(), groupMessageTxEvent.getTerminal()));
        sendMessage.setReceiverIds(groupMessageTxEvent.getRecvIds());
        sendMessage.setContent(groupMessageVO);
        client.sendGroupMessage(sendMessage);
        logger.info(
                "GroupMessageRocketMQEventConsumer | 发送群聊消息，发送者id: {}, 群组id: {}, 内容: {}",
                groupMessageTxEvent.getSendId(),
                groupMessageTxEvent.getGroupMessageDTO().getGroupId(),
                groupMessageTxEvent.getGroupMessageDTO().getContent()
        );
    }

    private GroupMessageTxEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, GroupMessageTxEvent.class);
    }
}
