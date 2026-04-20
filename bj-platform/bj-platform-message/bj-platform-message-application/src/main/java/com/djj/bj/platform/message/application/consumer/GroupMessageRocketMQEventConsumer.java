package com.djj.bj.platform.message.application.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.ai.dubbo.service.AIDubboService;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.GroupChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.common.threadpool.GroupMessageThreadPoolUtils;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Value("${bj.ai.userid:10000000001}")
    private Long aiUserId;

    @Value("${bj.ai.username:binghe}")
    private String aiUserName;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false, timeout = 60000, retries = 0)
    private AIDubboService aiDubboService;

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
        if (!CollectionUtil.isEmpty(groupMessageVO.getAtUserIds())
                && groupMessageVO.getAtUserIds().size() == 1
                && groupMessageVO.getAtUserIds().contains(aiUserId)) {
            GroupMessageThreadPoolUtils.execute(() -> {
                this.sendAIMessage(groupMessageVO, groupMessageTxEvent);
            });
        }
    }

    private void sendAIMessage(GroupMessageVO groupMessageVO, GroupMessageTxEvent groupMessageTxEvent) {
        // 没有@用户，或者@用户列表中没有指定的用户，直接return, double check
        if (CollectionUtil.isEmpty(groupMessageVO.getAtUserIds())
                || !groupMessageVO.getAtUserIds().contains(aiUserId)
                || groupMessageVO.getAtUserIds().size() > 1) {
            return;
        }
        try {
            logger.info("群聊消息发送AI消息开始");
            String AIMessage = aiDubboService.sendMessage(
                    buildGroupConversationId(groupMessageVO.getGroupId(), groupMessageVO.getSendId()),
                    groupMessageVO.getSendId(),
                    groupMessageTxEvent.getSendNickName(),
                    groupMessageVO.getContent()
            );
            logger.info("群聊消息发送AI消息，AI返回的消息内容: {}", AIMessage);

            GroupMessageVO groupAIMessageVO = new GroupMessageVO();
            groupAIMessageVO.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
            groupAIMessageVO.setGroupId(groupMessageVO.getGroupId());
            groupAIMessageVO.setSendId(aiUserId);
            groupAIMessageVO.setSendNickName(aiUserName);
            groupAIMessageVO.setContent(AIMessage);
            groupAIMessageVO.setType(groupMessageVO.getType());
            groupAIMessageVO.setAtUserIds(new ArrayList<>(List.of(groupMessageVO.getSendId())));
            groupAIMessageVO.setAtUserIdsStr(groupMessageVO.getSendNickName());
            groupAIMessageVO.setStatus(MessageStatus.UNSEND.getCode());
            groupAIMessageVO.setSendTime(new Date());

            GroupChat<GroupMessageVO> sendMessage = new GroupChat<>();
            sendMessage.setSender(new UserInfo(groupAIMessageVO.getSendId(), groupMessageTxEvent.getTerminal()));
            List<Long> recvIds = new ArrayList<>(groupMessageTxEvent.getRecvIds());
            // 去掉ai自身，加入用户id
            recvIds.remove(aiUserId);
            recvIds.add(groupMessageTxEvent.getSendId());
            recvIds = List.copyOf(recvIds);
            logger.info("群聊消息发送AI消息，接收者id列表: {}", recvIds);
            sendMessage.setReceiverIds(recvIds);
            sendMessage.setContent(groupAIMessageVO);
            client.sendGroupMessage(sendMessage);
            logger.info("群聊消息发送AI消息结束");
        } catch (IOException e) {
            logger.error("对接AI大模型消息异常: ", e);
        }
    }

    private GroupMessageTxEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, GroupMessageTxEvent.class);
    }

    private String buildGroupConversationId(Long groupId, Long userId) {
        return "group:" + groupId + ":user:" + userId;
    }
}
