package com.djj.bj.server.application.netty.processor.impl;

import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.ResponseType;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.model.ReceiveMessage;
import com.djj.bj.common.io.model.SendMessage;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.common.mq.MessageSenderService;
import com.djj.bj.server.application.netty.cache.UserChannelCtxCache;
import com.djj.bj.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 私聊消息处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.server.application.netty.processor.impl
 * @className PrivateMessageProcessor
 * @date 2025/7/7 16:40
 */
@Component
public class PrivateMessageProcessor implements MessageProcessor<ReceiveMessage> {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageProcessor.class);

    @Resource
    private MessageSenderService messageSenderService;

    @Override
    public void process(ReceiveMessage data) {
        UserInfo sender = data.getSender();
        // 私聊只有一个接收者
        UserInfo receiver = data.getReceivers().getFirst();

        logger.info("PrivateMessageProcessor.process: 发送者: {}, 接收者: {}, 消息内容: {}", sender.getUserId(), receiver.getUserId(), data.getContent());

        // 尝试推送消息
        try{
            ChannelHandlerContext channelHandlerContext = UserChannelCtxCache.getCtx(receiver.getUserId(), receiver.getTerminalType());
            if (channelHandlerContext != null) {
                // 推送消息
                SendMessage<?> sendMessage = new SendMessage<>(SystemInfoType.PRIVATE_CHAT.getCode(), data.getContent());
                channelHandlerContext.writeAndFlush(sendMessage);
                // 发送结果
                sendResult(data, ResponseType.SUCCESS);
                logger.info("PrivateMessageProcessor.process: 消息已发送给在线用户: {}", receiver.getUserId());
            } else {
                sendResult(data, ResponseType.MISSING_CHANNEL);
                logger.error("PrivateMessageProcessor.process: 未搜寻到Channel，无法发送消息给用户: {}", receiver.getUserId());
            }

        } catch (Exception e) {
            sendResult(data, ResponseType.UNKNOWN_ERROR);
            logger.error("PrivateMessageProcessor.process: 发送私聊消息失败, 发送者: {}, 接收者: {}, 错误信息: {}", sender.getUserId(), receiver.getUserId(), e.getMessage());
        }
    }

    private void sendResult(ReceiveMessage receiveMessage, ResponseType responseType) {
        if(receiveMessage.getSendResult()){
            SendResult<?> result = new SendResult<>(receiveMessage.getSender(),
                    receiveMessage.getReceivers().getFirst(), responseType.getCode(), receiveMessage.getContent());
            String sendKey = Constants.RESULT_MESSAGE_PRIVATE_QUEUE;
            result.setDestination(sendKey);
            messageSenderService.send(result);
        }
    }
}
