package com.djj.bj.platform.message.application.tx;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.message.application.service.GroupMessageService;
import com.djj.bj.platform.message.application.service.PrivateMessageService;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;
import com.djj.bj.platform.message.domain.event.MessageTxEvent;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 私聊事务消息监听器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.tx
 * @className PrivateMessageTxListener
 * @date 2025/8/5 15:08
 */
@Component
@RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
public class MessageTxListener implements RocketMQLocalTransactionListener {
    private final Logger logger = LoggerFactory.getLogger(MessageTxListener.class);

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private GroupMessageService groupMessageService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            MessageTxEvent messageTxEvent = this.getTxMessage(message);
            return switch (messageTxEvent.getMessageType()) {
                case PlatformConstants.TYPE_MESSAGE_PRIVATE -> this.executePrivateMessageLocalTransaction(message);
                case PlatformConstants.TYPE_MESSAGE_GROUP -> this.executeGroupMessageLocalTransaction(message);
                default -> this.executePrivateMessageLocalTransaction(message);
            };
        } catch (Exception e) {
            logger.info("MessageTxListener.executeLocalTransaction | 消息微服务提交本地事务异常 | {}", e.getMessage(), e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageTxEvent messageTxEvent = this.getPrivateTxMessage(message);
        logger.info("MessageTxListener.checkLocalTransaction | 消息微服务查询本地事务 | {}", messageTxEvent.getEventId());
        Boolean submitTransaction = Boolean.FALSE;
        switch (messageTxEvent.getMessageType()) {
            case PlatformConstants.TYPE_MESSAGE_PRIVATE ->
                    privateMessageService.checkExists(messageTxEvent.getEventId());
            case PlatformConstants.TYPE_MESSAGE_GROUP -> groupMessageService.checkExists(messageTxEvent.getEventId());
            default -> submitTransaction = privateMessageService.checkExists(messageTxEvent.getEventId());
        }
        return BooleanUtil.isTrue(submitTransaction) ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.UNKNOWN;
    }

    private RocketMQLocalTransactionState executeGroupMessageLocalTransaction(Message message) {
        GroupMessageTxEvent groupMessageTxEvent = this.getGroupTxMessage(message);
        boolean result = groupMessageService.saveGroupMessageTxEvent(groupMessageTxEvent);
        if (result) {
            logger.info("MessageTxListener.executeGroupMessageLocalTransaction | 消息微服务提交群聊本地事务成功 | {}", groupMessageTxEvent.getEventId());
            return RocketMQLocalTransactionState.COMMIT;
        }
        logger.info("MessageTxListener.executeGroupMessageLocalTransaction | 消息微服务提交群聊本地事务失败 | {}", groupMessageTxEvent.getEventId());
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    private RocketMQLocalTransactionState executePrivateMessageLocalTransaction(Message message) {
        PrivateMessageTxEvent privateMessageTxEvent = this.getPrivateTxMessage(message);
        boolean result = privateMessageService.savePrivateMessageTxEvent(privateMessageTxEvent);
        if (result) {
            logger.info("MessageTxListener.executePrivateMessageLocalTransaction | 消息微服务提交私聊本地事务成功 | {}", privateMessageTxEvent.getEventId());
            return RocketMQLocalTransactionState.COMMIT;
        }
        logger.info("MessageTxListener.executePrivateMessageLocalTransaction | 消息微服务提交私聊本地事务失败 | {}", privateMessageTxEvent.getEventId());
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    private MessageTxEvent getTxMessage(Message msg) {
        String messageString = new String((byte[]) msg.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String txStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(txStr, MessageTxEvent.class);
    }

    private PrivateMessageTxEvent getPrivateTxMessage(Message msg) {
        String messageString = new String((byte[]) msg.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String txStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(txStr, PrivateMessageTxEvent.class);
    }

    private GroupMessageTxEvent getGroupTxMessage(Message msg) {
        String messageString = new String((byte[]) msg.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String txStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(txStr, GroupMessageTxEvent.class);
    }
}
