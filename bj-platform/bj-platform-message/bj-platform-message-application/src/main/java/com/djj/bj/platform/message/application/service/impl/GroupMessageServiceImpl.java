package com.djj.bj.platform.message.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.GroupChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.common.mq.MessageSenderService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.GroupMessageDTO;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.enums.MessageType;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.common.threadpool.GroupMessageThreadPoolUtils;
import com.djj.bj.platform.common.utils.DateTimeUtils;
import com.djj.bj.platform.dubbo.group.GroupDubboService;
import com.djj.bj.platform.message.application.service.GroupMessageService;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;
import com.djj.bj.platform.message.domain.service.GroupMessageDomainService;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 群聊消息服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service.impl
 * @className GroupMessageServiceImpl
 * @date 2025/8/7 10:41
 */
@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageServiceImpl.class);

    @Resource
    private Client client;

    @Resource
    private MessageSenderService messageSenderService;

    @Resource
    private GroupMessageDomainService groupMessageDomainService;

    @Resource
    private DistributeCacheService distributeCacheService;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false)
    private GroupDubboService groupDubboService;

    @Override
    public Long sendMessage(GroupMessageDTO dto) {
        UserSession userSession = SessionContext.getUserSession();
        boolean isExists = groupDubboService.isExists(dto.getGroupId());
        if (!isExists) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群组不存在或者已经解散");
        }
        // 当前用户是否在群组中
        GroupMemberSimpleVO groupMemberSimpleVO = null;
        try {
            groupMemberSimpleVO = groupDubboService.getGroupMemberSimpleVO(new GroupParams(userSession.getUserId(), dto.getGroupId()));
        } catch (Exception e) {
            logger.info("GroupMessageServiceImpl | GroupMemberSimpleVO获取异常 | {}", e.getMessage());
        }
        if (Objects.isNull(groupMemberSimpleVO) || groupMemberSimpleVO.getQuit()) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "你已不在该群组中，无法发送消息");
        }
        // 获取群组中的群成员列表
        List<Long> userIds = groupDubboService.getUserIdsByGroupId(dto.getGroupId());
        if (CollectionUtil.isEmpty(userIds)) {
            userIds = Collections.emptyList();
        }
        // 排除自己
        userIds = userIds.stream()
                .filter(userId -> !userSession.getUserId().equals(userId))
                .toList();
        // 消息ID
        Long messageId = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        // 构造事务事件
        GroupMessageTxEvent groupMessageTxEvent = new GroupMessageTxEvent(
                messageId,
                userSession.getUserId(),
                groupMemberSimpleVO.getAliasName(),
                userSession.getTerminalType(),
                new Date(),
                PlatformConstants.TOPIC_GROUP_TX_MESSAGE,
                userIds,
                dto);
        TransactionSendResult sendResult = messageSenderService.sendMessageInTransaction(groupMessageTxEvent, null);
        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
            logger.error("GroupMessageServiceImpl | 发送事务消息失败 | 参数:{}", dto);
        }
        return messageId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveGroupMessageTxEvent(GroupMessageTxEvent groupMessageTxEvent) {
        return groupMessageDomainService.saveGroupMessageTxEvent(groupMessageTxEvent);
    }

    @Override
    public boolean checkExists(Long messageId) {
        return groupMessageDomainService.checkExists(messageId);
    }

    @Override
    public void pullUnreadMessage() {
        UserSession userSession = SessionContext.getUserSession();
        List<GroupMemberSimpleVO> groupMemberSimpleVOList = groupDubboService.getGroupMemberSimpleVOList(userSession.getUserId());
        if (CollectionUtil.isEmpty(groupMemberSimpleVOList)) {
            return;
        }
        groupMemberSimpleVOList.parallelStream().forEach(groupMemberSimpleVO -> {
            String key = String.join(Constants.REDIS_KEY_SPLIT, Constants.GROUP_MESSAGE_READ_POSITION, groupMemberSimpleVO.getGroupId().toString(), userSession.getUserId().toString());
            String maxReadedIdStr = distributeCacheService.get(key);
            Long maxReadedId = StrUtil.isEmpty(maxReadedIdStr) ? 0L : Long.parseLong(maxReadedIdStr);
            List<GroupMessageVO> unreadGroupMessageList = groupMessageDomainService.getUnreadGroupMessageList(
                    groupMemberSimpleVO.getGroupId(),
                    groupMemberSimpleVO.getCreateTime(),
                    userSession.getUserId(),
                    MessageStatus.WITHDRAW.getCode(),
                    maxReadedId,
                    PlatformConstants.PULL_HISTORY_MESSAGE_LIMIT_COUNR
            );
            if (!CollectionUtil.isEmpty(unreadGroupMessageList)) {
                GroupMessageThreadPoolUtils.execute(() -> {
                    for (GroupMessageVO groupMessageVO : unreadGroupMessageList) {
                        GroupChat<GroupMessageVO> groupChat = new GroupChat<>();
                        groupChat.setSender(new UserInfo(userSession.getUserId(), userSession.getTerminalType()));
                        // 只推送给自己当前终端
                        groupChat.setReceiverIds(Collections.singletonList(userSession.getUserId()));
                        groupChat.setReceiverTerminals(Collections.singletonList(userSession.getTerminalType()));
                        groupChat.setContent(groupMessageVO);
                        client.sendGroupMessage(groupChat);
                    }
                });
                logger.info("GroupMessageServiceImpl.pullUnreadMessage | 异步拉取群聊消息 | 群组ID: {}, 用户ID: {}, 未读消息数量: {}",
                        groupMemberSimpleVO.getGroupId(), userSession.getUserId(), unreadGroupMessageList.size());
            }


        });
    }

    @Override
    public List<GroupMessageVO> loadMessage(Long minId) {
        UserSession userSession = SessionContext.getUserSession();
        List<Long> groupIds = groupDubboService.getGroupIdsByUserId(userSession.getUserId());
        if (CollectionUtil.isEmpty(groupIds)) {
            return Collections.emptyList();
        }
        // 拉取最近1个月的群聊消息
        Date minDate = DateTimeUtils.addMonths(new Date(), -1);
        List<GroupMessageVO> groupMessageVOList = groupMessageDomainService.loadGroupMessageList(
                minId,
                minDate,
                groupIds,
                MessageStatus.WITHDRAW.getCode(),
                PlatformConstants.PULL_HISTORY_MESSAGE_LIMIT_COUNR
        );
        if (CollectionUtil.isEmpty(groupMessageVOList)) {
            return Collections.emptyList();
        }
        List<GroupMessageVO> vos = groupMessageVOList.stream().peek(m -> {
            List<String> atIds = StrUtil.split(m.getAtUserIdsStr(), Constants.USER_ID_SPLIT);
            m.setAtUserIds(atIds.stream().map(Long::parseLong).toList());
        }).toList();
        List<String> keys = groupIds.stream().map(id -> String.join(
                Constants.REDIS_KEY_SPLIT,
                Constants.GROUP_MESSAGE_READ_POSITION,
                id.toString(),
                userSession.getUserId().toString()
        )).toList();
        List<String> sendPos = distributeCacheService.multiGet(keys);
        for (int idx = 0; idx < groupIds.size(); idx++) {
            Long id = groupIds.get(idx);
            String str = sendPos.get(idx);
            Long sendMaxId = StrUtil.isEmpty(str) ? 0L : Long.parseLong(str);
            vos.stream().filter(vo -> vo.getGroupId().equals(id)).forEach(vo -> {
                if (vo.getId() <= sendMaxId) {
                    // 已读
                    vo.setStatus(MessageStatus.READED.getCode());
                } else {
                    // 未推送
                    vo.setStatus(MessageStatus.UNSEND.getCode());
                }
            });
        }
        return vos;
    }

    @Override
    public List<GroupMessageVO> findHistoryMessage(Long groupId, Long page, Long size) {
        page = page > 0 ? page : PlatformConstants.DEFAULT_PAGE;
        size = size > 0 ? size : PlatformConstants.DEFAULT_PAGE_SIZE;
        Long userId = SessionContext.getUserSession().getUserId();
        long stIdx = (page - 1) * size;
        GroupMemberSimpleVO groupMember = groupDubboService.getGroupMemberSimpleVO(new GroupParams(userId, groupId));
        if (groupMember == null || groupMember.getQuit()) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "您已不在群聊中");
        }
        List<GroupMessageVO> historyMessage = groupMessageDomainService.getHistoryMessage(groupId, groupMember.getCreateTime(), MessageStatus.WITHDRAW.getCode(), stIdx, size);
        if (CollectionUtil.isEmpty(historyMessage)) {
            historyMessage = Collections.emptyList();
        }
        logger.info("GroupMessageServiceImpl.findHistoryMessage | 拉取群聊历史消息 | 群组ID: {}, 用户ID: {}, 页码: {}, 每页大小: {}, 历史消息数量: {}",
                groupId, userId, page, size, historyMessage.size());
        return historyMessage;
    }

    @Override
    public void readedMessage(Long groupId) {
        UserSession session = SessionContext.getUserSession();
        // 取出最后的消息id
        Long maxMessageId = groupMessageDomainService.getMaxMessageId(groupId);
        if (maxMessageId == null) {
            return;
        }
        // 推送消息给自己的其他终端
        GroupMessageVO msgInfo = new GroupMessageVO();
        msgInfo.setType(MessageType.READED.getCode());
        msgInfo.setSendTime(new Date());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setGroupId(groupId);
        GroupChat<GroupMessageVO> sendMessage = new GroupChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setSendToSelfOtherTerminals(true);
        sendMessage.setContent(msgInfo);
        sendMessage.setReturnResult(false);
        client.sendGroupMessage(sendMessage);
        // 记录已读消息位置
        String key = StrUtil.join(Constants.REDIS_KEY_SPLIT, Constants.GROUP_MESSAGE_READ_POSITION, groupId, session.getUserId());
        distributeCacheService.set(key, String.valueOf(maxMessageId));
    }

    @Override
    public void withdrawMessage(Long id) {
        UserSession session = SessionContext.getUserSession();
        GroupMessageVO msg = groupMessageDomainService.getGroupMessageById(id);
        if (Objects.isNull(msg)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "消息不存在");
        }
        if (!msg.getSendId().equals(session.getUserId())) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "这条消息不是由您发送,无法撤回");
        }
        if (System.currentTimeMillis() - msg.getSendTime().getTime() > Constants.ALLOW_RECALL_SECOND * 1000) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "消息已发送超过5分钟，无法撤回");
        }
        GroupMemberSimpleVO member = groupDubboService.getGroupMemberSimpleVO(new GroupParams(session.getUserId(), msg.getGroupId()));
        if (Objects.isNull(member) || member.getQuit()) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "您已不在群里，无法撤回消息");
        }
        //更新消息状态
        groupMessageDomainService.updateStatus(MessageStatus.WITHDRAW.getCode(), id);
        GroupMessageThreadPoolUtils.execute(() -> {
            List<Long> userIds = groupDubboService.getUserIdsByGroupId(msg.getGroupId());
            // 不用发给自己
            userIds = userIds.stream().filter(uid -> !session.getUserId().equals(uid)).collect(Collectors.toList());
            msg.setType(MessageType.WITHDRAW.getCode());
            String content = String.format("'%s'撤回了一条消息", member.getAliasName());
            msg.setContent(content);
            msg.setSendTime(new Date());

            GroupChat<GroupMessageVO> sendMessage = new GroupChat<>();
            sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
            sendMessage.setReceiverIds(userIds);
            sendMessage.setContent(msg);
            sendMessage.setReturnResult(false);
            sendMessage.setSendToSelfOtherTerminals(false);
            client.sendGroupMessage(sendMessage);

            // 推给自己其他终端
            msg.setContent("你撤回了一条消息");
            sendMessage.setSendToSelfOtherTerminals(true);
            sendMessage.setReceiverIds(Collections.emptyList());
            sendMessage.setReceiverTerminals(Collections.emptyList());
            client.sendGroupMessage(sendMessage);
            logger.info("GroupMessageServiceImpl.withdrawMessage | 撤回群聊消息，发送id:{},群聊id:{},内容:{}", session.getUserId(), msg.getGroupId(), msg.getContent());
        });
    }
}
