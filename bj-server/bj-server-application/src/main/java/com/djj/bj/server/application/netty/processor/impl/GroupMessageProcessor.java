package com.djj.bj.server.application.netty.processor.impl;

import com.djj.bj.server.application.netty.cache.UserChannelCtxCache;
import com.djj.bj.server.application.netty.processor.MessageProcessor;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.ResponseType;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.model.ReceiveMessage;
import com.djj.bj.common.io.model.SendMessage;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.common.mq.MessageSenderService;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * 群聊消息处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.processor.impl
 * @className GroupMessageProcessor
 * @date 2025/7/7 22:52
 */
public class GroupMessageProcessor implements MessageProcessor<ReceiveMessage> {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageProcessor.class);

    @Resource
    private MessageSenderService messageSenderService;

    @Async
    @Override
    public void process(ReceiveMessage receiveMessage) {
        UserInfo sender = receiveMessage.getSender();
        List<UserInfo> receivers = receiveMessage.getReceivers();

        logger.info("GroupMessageProcessor.process: 接收与处理群聊消息, sender: {}, receivers_num: {}, content: {}", sender.getUserId(), receivers.size(), receiveMessage.getContent());

        receivers.forEach(receiver -> {
            try{
                ChannelHandlerContext channelHandlerContext = UserChannelCtxCache.getCtx(receiver.getUserId(),receiver.getTerminalType());
                if(channelHandlerContext != null){
                    // 推送消息到对应的用户
                    SendMessage<?> sendMessage = new SendMessage<>(SystemInfoType.GROUP_CHAT.getCode(), receiveMessage.getContent());
                    channelHandlerContext.writeAndFlush(sendMessage);
                    // 确认
                    sendResult(receiveMessage,receiver, ResponseType.SUCCESS);
                }else{
                    // 未找到连接信息
                    sendResult(receiveMessage,receiver, ResponseType.MISSING_CHANNEL);
                    logger.error("GroupMessageProcessor.process: 未找到用户channel, sender: {}, receiver: {}, content: {}", sender.getUserId(),  receiver.getUserId(), receiveMessage.getContent());
                }
            }catch (Exception e){
                sendResult(receiveMessage, receiver, ResponseType.UNKNOWN_ERROR);
                logger.error("GroupMessageProcessor.process: 处理群聊消息异常, sender: {}, receiver: {}, content: {}, error: {}", sender.getUserId(), receiver.getUserId(), receiveMessage.getContent(), e.getMessage());
            }
        });
    }

    private void sendResult(ReceiveMessage receiveMessage, UserInfo userInfo, ResponseType responseType) {
        if(receiveMessage.getSendResult()){
            SendResult<?> result = new SendResult<>(receiveMessage.getSender(), userInfo, responseType.getCode(),receiveMessage.getContent());
            result.setDestination(Constants.RESULT_MESSAGE_GROUP_QUEUE);
            messageSenderService.send(result);
        }
    }
}
