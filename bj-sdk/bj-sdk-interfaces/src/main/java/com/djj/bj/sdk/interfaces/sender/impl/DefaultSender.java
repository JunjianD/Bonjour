package com.djj.bj.sdk.interfaces.sender.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.enums.ResponseType;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.model.PrivateChat;
import com.djj.bj.common.io.model.ReceiveMessage;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.common.mq.MessageSenderService;
import com.djj.bj.sdk.infrastructure.multicaster.MessageListenerMulticaster;
import com.djj.bj.sdk.interfaces.sender.Sender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 默认消息发送器实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.interfaces.sender.impl
 * @className DefaultSender
 * @date 2025/7/9 16:21
 */
@Service
public class DefaultSender implements Sender {
    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private MessageSenderService messageSenderService;

    @Resource
    private MessageListenerMulticaster messageListenerMulticaster;

    @Override
    public <T> void sendPrivateMessage(PrivateChat<T> privateChat) {
        if(privateChat == null){
            return;
        }
        // 向用户终端发送数据
        List<Integer> receiveTerminals = privateChat.getReceiverTerminals();
        if(!CollectionUtil.isEmpty(receiveTerminals)){
            this.sendPrivateMessageToTargetUser(privateChat, receiveTerminals);
            this.sendPrivateMessageToSelf(privateChat, receiveTerminals);
        }
    }

    /**
     * 发送消息到自己的其他终端
     *
     * @param privateChat 消息实体
     * @param <T>         消息内容类型
     */
    private <T> void sendPrivateMessageToSelf(PrivateChat<T> privateChat, List<Integer> receiveTerminals) {
        // 发送消息到自己的其他终端
        if(BooleanUtil.isTrue(privateChat.getSendToSelfOtherTerminals())){
            receiveTerminals.forEach(receiveTerminal -> {
                // 排除当前终端类型
                if(!privateChat.getSender().getTerminalType().equals(receiveTerminal)){
                    String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, privateChat.getSender().getUserId().toString(), receiveTerminal.toString());
                    String serverId = distributeCacheService.get(redisKey);

                    if(!StrUtil.isEmpty(serverId)){
                        String sendKey = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_PRIVATE_QUEUE,serverId);
                        ReceiveMessage receiveMessage = new ReceiveMessage(
                                SystemInfoType.PRIVATE_CHAT.getCode(),
                                privateChat.getSender(),
                                Collections.singletonList(new UserInfo(privateChat.getSender().getUserId(),receiveTerminal)),
                                false,
                                privateChat.getContent()
                        );
                        receiveMessage.setDestination(sendKey);
                        messageSenderService.send(receiveMessage);
                    }

                }
            });
        }
    }

    private <T> void sendPrivateMessageToTargetUser(PrivateChat<T> privateChat,List<Integer> receiveTerminals) {
        receiveTerminals.forEach(receiveTerminal -> {
            // 发送消息到目标用户
            String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, privateChat.getReceiverId().toString(), receiveTerminal.toString());
            String serverId = distributeCacheService.get(redisKey);

            if(!StrUtil.isEmpty(serverId)){
                String sendKey = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_PRIVATE_QUEUE,serverId);
                ReceiveMessage receiveMessage = new ReceiveMessage(
                        SystemInfoType.PRIVATE_CHAT.getCode(),
                        privateChat.getSender(),
                        Collections.singletonList(new UserInfo(privateChat.getReceiverId(),receiveTerminal)),
                        privateChat.getReturnResult(),
                        privateChat.getContent()
                );
                receiveMessage.setDestination(sendKey);
                messageSenderService.send(receiveMessage);
            }else if(BooleanUtil.isTrue(privateChat.getReturnResult())){
                // 如果目标用户不在线,则返回发送结果
                SendResult<T> result = new SendResult<>(privateChat.getSender(),new UserInfo(privateChat.getReceiverId(), receiveTerminal), ResponseType.OFFLINE.getCode(), privateChat.getContent());
                messageListenerMulticaster.multicast(ListeningType.PRIVATE_MESSAGE,result);
            }
        });
    }
}
