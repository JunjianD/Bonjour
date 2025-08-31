package com.djj.bj.platform.message.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.PrivateChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.common.mq.MessageSenderService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.PrivateMessageDTO;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.enums.MessageType;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.common.threadpool.PrivateMessageThreadPoolUtils;
import com.djj.bj.platform.common.utils.DateTimeUtils;
import com.djj.bj.platform.dubbo.friend.FriendDubboService;
import com.djj.bj.platform.message.application.service.PrivateMessageService;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;
import com.djj.bj.platform.message.domain.service.PrivateMessageDomainService;
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

/**
 * 私聊消息服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service.impl
 * @className PrivateMessageService
 * @date 2025/8/5 14:19
 */
@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageServiceImpl.class);

    @Resource
    private Client client;

    @Resource
    private MessageSenderService messageSenderService;

    @Resource
    private PrivateMessageDomainService privateMessageDomainService;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false)
    private FriendDubboService friendDubboService;


    @Override
    public Long sendMessage(PrivateMessageDTO dto) {
        UserSession session = SessionContext.getUserSession();
        Boolean isFriend = friendDubboService.isFriend(session.getUserId(), dto.getRecvId());
        if (BooleanUtil.isFalse(isFriend)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "对方不是你的好友，无法发送消息");
        }
        Long messageId = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        // 组装事务消息数据
        PrivateMessageTxEvent privateMessageTxEvent = new PrivateMessageTxEvent(
                messageId,
                session.getUserId(),
                session.getTerminalType(),
                PlatformConstants.TOPIC_PRIVATE_TX_MESSAGE,
                new Date(),
                dto
        );
        TransactionSendResult sendResult = messageSenderService.sendMessageInTransaction(privateMessageTxEvent, null);
        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
            logger.error("PrivateMessageServiceImpl | 发送事务消息失败 | 参数:{}", dto);
        }
        return messageId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savePrivateMessageTxEvent(PrivateMessageTxEvent privateMessageTxEvent) {
        return privateMessageDomainService.savePrivateMessageTxEvent(privateMessageTxEvent);
    }

    @Override
    public boolean checkExists(Long messageId) {
        return privateMessageDomainService.checkExists(messageId);
    }

    @Override
    public void pullUnreadMessage() {
        UserSession userSession = SessionContext.getUserSession();
        if (!client.isOnline(userSession.getUserId())) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "用户未建立连接");
        }
        List<Long> friendIdList = friendDubboService.getFriendIdList(userSession.getUserId());
        if (CollectionUtil.isEmpty(friendIdList)) {
            return;
        }
        List<PrivateMessageVO> privateMessageList = privateMessageDomainService.getPrivateMessageVOList(userSession.getUserId(), friendIdList);
        int messageSize = 0;
        if (!CollectionUtil.isEmpty(privateMessageList)) {
            messageSize = privateMessageList.size();
            privateMessageList.parallelStream().forEach((privateMessageVO) -> {
                // 推送消息
                PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
                sendMessage.setSender(new UserInfo(userSession.getUserId(), userSession.getTerminalType()));
                sendMessage.setReceiverId(userSession.getUserId());
                sendMessage.setReceiverTerminals(Collections.singletonList(userSession.getTerminalType()));
                sendMessage.setSendToSelfOtherTerminals(false);
                sendMessage.setContent(privateMessageVO);
                client.sendPrivateMessage(sendMessage);
            });
        }
        logger.info("PrivateMessageServiceImpl.pullUnreadMessage | 拉取未读私聊消息，用户id:{},数量:{}", userSession.getUserId(), messageSize);
    }

    @Override
    public List<PrivateMessageVO> loadMessage(Long minId) {
        UserSession session = SessionContext.getUserSession();
        List<Long> friendIdList = friendDubboService.getFriendIdList(session.getUserId());
        if (CollectionUtil.isEmpty(friendIdList)) {
            return Collections.emptyList();
        }
        Date minDate = DateTimeUtils.addMonths(new Date(), -1);
        List<PrivateMessageVO> privateMessageList = privateMessageDomainService.loadMessage(session.getUserId(), minId, minDate, friendIdList, PlatformConstants.PULL_HISTORY_MESSAGE_LIMIT_COUNR);
        if (CollectionUtil.isEmpty(privateMessageList)) {
            return Collections.emptyList();
        }
        PrivateMessageThreadPoolUtils.execute(() -> {
            // 更新发送状态
            List<Long> ids = privateMessageList.stream()
                    .filter(m -> !m.getSendId().equals(session.getUserId()) && m.getStatus().equals(MessageStatus.UNSEND.getCode()))
                    .map(PrivateMessageVO::getId)
                    .toList();
            if (!CollectionUtil.isEmpty(ids)) {
                privateMessageDomainService.batchUpdatePrivateMessageStatus(MessageStatus.SENDED.getCode(), ids);
            }
        });
        logger.info("PrivateMessageServiceImpl.loadMessage | 拉取消息，用户id:{},数量:{}", session.getUserId(), privateMessageList.size());
        return privateMessageList;
    }

    @Override
    public List<PrivateMessageVO> getHistoryMessage(Long friendId, Long page, Long size) {
        page = page > 0 ? page : 1;
        size = size > 0 ? size : 10;
        Long userId = SessionContext.getUserSession().getUserId();
        long stIdx = (page - 1) * size;
        List<PrivateMessageVO> privateMessageList = privateMessageDomainService.loadMessageByUserIdAndFriendId(userId, friendId, stIdx, size);
        if (CollectionUtil.isEmpty(privateMessageList)) {
            privateMessageList = Collections.emptyList();
        }
        logger.info("PrivateMessageServiceImpl.getHistoryMessage | 拉取聊天记录，用户id:{},好友id:{}，数量:{}", userId, friendId, privateMessageList.size());
        return privateMessageList;
    }

    @Override
    public void readedMessage(Long friendId) {
        if (friendId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR, "触发消息已读时，好友ID不能为空");
        }
        UserSession session = SessionContext.getUserSession();
        // 推送消息
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.READED.getCode());
        msgInfo.setSendTime(new Date());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setRecvId(friendId);
        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(friendId);
        sendMessage.setSendToSelfOtherTerminals(true);
        sendMessage.setContent(msgInfo);
        sendMessage.setReturnResult(false);
        client.sendPrivateMessage(sendMessage);
        PrivateMessageThreadPoolUtils.execute(() -> {
            privateMessageDomainService.updateMessageStatus(MessageStatus.READED.getCode(), friendId, session.getUserId());
        });
        logger.info("PrivateMessageServiceImpl.readedMessage | 消息已读，接收方id:{},发送方id:{}", session.getUserId(), friendId);
    }

    @Override
    public void withdrawMessage(Long id) {
        UserSession session = SessionContext.getUserSession();
        if (id == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        PrivateMessageVO privateMessage = privateMessageDomainService.getPrivateMessageById(id);
        if (privateMessage == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "消息不存在");
        }
        if (!privateMessage.getSendId().equals(session.getUserId())) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "这条消息不是由您发送,无法撤回");
        }
        if (System.currentTimeMillis() - privateMessage.getSendTime().getTime() > Constants.ALLOW_RECALL_SECOND * 1000) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "消息已发送超过5分钟，无法撤回");
        }
        //更新消息状态为已撤回
        privateMessageDomainService.updateMessageStatusById(MessageStatus.WITHDRAW.getCode(), id);
        PrivateMessageThreadPoolUtils.execute(() -> {
            // 推送消息
            privateMessage.setType(MessageType.WITHDRAW.getCode());
            privateMessage.setSendTime(new Date());
            privateMessage.setContent("对方撤回了一条消息");

            PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
            sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
            sendMessage.setReceiverId(privateMessage.getRecvId());
            sendMessage.setSendToSelfOtherTerminals(false);
            sendMessage.setContent(privateMessage);
            sendMessage.setReturnResult(false);
            client.sendPrivateMessage(sendMessage);

            // 推给自己其他终端
            privateMessage.setContent("你撤回了一条消息");
            sendMessage.setSendToSelfOtherTerminals(true);
            sendMessage.setReceiverTerminals(Collections.emptyList());
            client.sendPrivateMessage(sendMessage);
            logger.info("PrivateMessageServiceImpl.withdrawMessage | 撤回私聊消息，发送id:{},接收id:{}，内容:{}", privateMessage.getSendId(), privateMessage.getRecvId(), privateMessage.getContent());
        });
    }

    @Override
    public Long getMaxReadedId(Long friendId) {
        UserSession session = SessionContext.getUserSession();
        if (session == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        Long maxReadedId = privateMessageDomainService.getMaxReadedId(session.getUserId(), friendId);
        if (maxReadedId == null) {
            maxReadedId = -1L;
        }
        return maxReadedId;
    }
}
