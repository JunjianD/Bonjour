package com.djj.bj.sdk.interfaces.sender.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.enums.ResponseType;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.enums.TerminalType;
import com.djj.bj.common.io.model.*;
import com.djj.bj.common.mq.MessageSenderService;
import com.djj.bj.sdk.infrastructure.multicaster.MessageListenerMulticaster;
import com.djj.bj.sdk.interfaces.sender.Sender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    public <T> void sendGroupMessage(GroupChat<T> groupChat) {
        Map<String, UserInfo> userConnectionMap = this.getUserConnectionMap(groupChat);
        if(CollectionUtil.isEmpty(userConnectionMap)){
            return;
        }
        // 批量拉取数据,获取服务器id
        List<String> serverIds = distributeCacheService.multiGet(userConnectionMap.keySet());
        if(CollectionUtil.isEmpty(serverIds)){
            return;
        }
        // 按照服务器id分组
        Map<Integer,List<UserInfo>> serverIdMap = new HashMap<>(serverIds.size());
        // 离线用户
        List<UserInfo> offlineUsers = new LinkedList<>();

        int idx = 0;
        for(Map.Entry<String,UserInfo> entry : userConnectionMap.entrySet()){
            String serverIdStr = serverIds.get(idx++);
            if(!StrUtil.isEmpty(serverIdStr)){
                List<UserInfo> userInfos = serverIdMap.computeIfAbsent(Integer.parseInt(serverIdStr),k -> new LinkedList<>());
                userInfos.add(entry.getValue());
            }else{
                offlineUsers.add(entry.getValue());
            }
        }
        List<Integer> receiveTerminals = groupChat.getReceiverTerminals();
        this.sendGroupChatToTargetUsers(serverIdMap,offlineUsers, groupChat);
        this.sendGroupChatToSelf(groupChat, receiveTerminals);

    }

    @Override
    public Map<Long, List<TerminalType>> getOnlineTerminalMap(List<Long> userIds) {
        if(CollectionUtil.isEmpty(userIds)){
            return Collections.emptyMap();
        }
        Map<String, UserInfo> userInfoMap = new HashMap<>(userIds.size());
        for(Long userId : userIds){
            for(Integer terminalType : TerminalType.getAllCodes()){
                String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, userId.toString(), terminalType.toString());
                userInfoMap.put(redisKey, new UserInfo(userId, terminalType));
            }
        }

        // 批量拉取数据,获取服务器id
        List<String> serverIds = distributeCacheService.multiGet(userInfoMap.keySet());
        int idx = 0;
        Map<Long,List<TerminalType>> onlineTerminalMap = new HashMap<>(userIds.size());
        for(Map.Entry<String,UserInfo> entry : userInfoMap.entrySet()){
            String serverIdStr = serverIds.get(idx++);
            if(!StrUtil.isEmpty(serverIdStr)){
                UserInfo userInfo = entry.getValue();
                List<TerminalType> onlineTerminalTypes = onlineTerminalMap.computeIfAbsent(userInfo.getUserId(), k -> new LinkedList<>());
                onlineTerminalTypes.add(TerminalType.fromCode(userInfo.getTerminalType()));
            }
        }
        return onlineTerminalMap;
    }

    @Override
    public Boolean isOnline(Long userId) {
        String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, userId.toString(),"*");
        Set<String> keysSet = distributeCacheService.getAllKeys(redisKey);
        return !CollectionUtil.isEmpty(keysSet);
    }

    @Override
    public List<Long> getOnlineUserIds(List<Long> userIds) {
        return new LinkedList<>(this.getOnlineTerminalMap(userIds).keySet());
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
                        String sendKey = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_PRIVATE_QUEUE, serverId);
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

    /**
     * 发送私聊消息到目标用户
     *
     * @param privateChat      私聊消息实体
     * @param receiveTerminals 接收终端列表
     * @param <T>              消息内容类型
     */
    private <T> void sendPrivateMessageToTargetUser(PrivateChat<T> privateChat,List<Integer> receiveTerminals) {
        receiveTerminals.forEach(receiveTerminal -> {
            // 发送消息到目标用户
            String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, privateChat.getReceiverId().toString(), receiveTerminal.toString());
            String serverId = distributeCacheService.get(redisKey);

            if(!StrUtil.isEmpty(serverId)){
                String sendKey = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_PRIVATE_QUEUE, serverId);
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

    /**
     * 获取用户信息映射
     *
     * @param groupChat 群聊消息实体
     * @param <T>       消息内容类型
     * @return 用户信息映射
     */
    private <T> Map<String, UserInfo> getUserConnectionMap(GroupChat<T> groupChat) {
        Map<String, UserInfo> userConnectionMap = new HashMap<>();
        if(groupChat==null){
            return userConnectionMap;
        }
        for(Integer terminal : groupChat.getReceiverTerminals()){
            groupChat.getReceiverIds().forEach(receiveId -> {
                String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, receiveId.toString(), terminal.toString());
                userConnectionMap.put(redisKey,new UserInfo(receiveId, terminal));
            });
        }
        return userConnectionMap;
    }

    /**
     * 发送群聊消息到自己的其他终端
     *
     * @param groupChat       群聊消息实体
     * @param receiveTerminals 接收终端列表
     * @param <T>              消息内容类型
     */
    private <T> void sendGroupChatToSelf(GroupChat<T> groupChat,List<Integer> receiveTerminals) {
        // 发送消息到自己的其他终端
        if(BooleanUtil.isTrue(groupChat.getSendToSelfOtherTerminals())){
            receiveTerminals.forEach(receiveTerminal -> {
                // 排除当前终端类型
                if(!groupChat.getSender().getTerminalType().equals(receiveTerminal)){
                    String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, groupChat.getSender().getUserId().toString(), receiveTerminal.toString());
                    String serverId = distributeCacheService.get(redisKey);

                    if(!StrUtil.isEmpty(serverId)){
                        String sendKey = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_GROUP_QUEUE, serverId);
                        ReceiveMessage receiveMessage = new ReceiveMessage(
                                SystemInfoType.GROUP_CHAT.getCode(),
                                groupChat.getSender(),
                                Collections.singletonList(new UserInfo(groupChat.getSender().getUserId(),receiveTerminal)),
                                false,
                                groupChat.getContent()
                        );
                        receiveMessage.setDestination(sendKey);
                        messageSenderService.send(receiveMessage);
                    }

                }
            });
        }
    }

    /**
     * 发送群聊消息到目标用户
     *
     * @param serverIdMap   服务器ID映射
     * @param offlineUsers  离线用户列表
     * @param groupChat     群聊消息实体
     * @param <T>           消息内容类型
     */
    private <T> void sendGroupChatToTargetUsers(Map<Integer,List<UserInfo>> serverIdMap, List<UserInfo> offlineUsers, GroupChat<T> groupChat) {
        for(Map.Entry<Integer,List<UserInfo>> entry : serverIdMap.entrySet()){
            String sendKey = String.join(Constants.MESSAGE_KEY_SPLIT, Constants.MESSAGE_GROUP_QUEUE, String.valueOf(entry.getKey()));
            ReceiveMessage receiveMessage = new ReceiveMessage(
                    SystemInfoType.GROUP_CHAT.getCode(),
                    groupChat.getSender(),
                    new LinkedList<>(entry.getValue()),
                    groupChat.getReturnResult(),
                    groupChat.getContent()
            );
            receiveMessage.setDestination(sendKey);
            messageSenderService.send(receiveMessage);
        }
        // 处理离线用户, 返回信息
        if(BooleanUtil.isTrue(groupChat.getReturnResult())){
            offlineUsers.forEach(offlineUser -> {
                SendResult<T> result = new SendResult<>(groupChat.getSender(), offlineUser, ResponseType.OFFLINE.getCode(), groupChat.getContent());
                messageListenerMulticaster.multicast(ListeningType.GROUP_MESSAGE, result);
            });
        }
    }
}
