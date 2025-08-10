package com.djj.bj.platform.message.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.entity.PrivateMessage;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;
import com.djj.bj.platform.message.domain.repository.PrivateMessageRepository;
import com.djj.bj.platform.message.domain.service.PrivateMessageDomainService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 私聊消息领域服务接口实现
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.service.impl
 * @className PrivateMessageDomainServiceImpl
 * @date 2025/8/5 11:53
 */
@Service
public class PrivateMessageDomainServiceImpl extends ServiceImpl<PrivateMessageRepository, PrivateMessage> implements PrivateMessageDomainService {
    @Override
    public boolean savePrivateMessageTxEvent(PrivateMessageTxEvent privateMessageTxEvent) {
        if (privateMessageTxEvent == null || privateMessageTxEvent.getPrivateMessageDTO() == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "私聊消息为空");
        }
        // 保存消息
        PrivateMessage privateMessage = BeanUtils.copyProperties(privateMessageTxEvent.getPrivateMessageDTO(), PrivateMessage.class);
        if (privateMessage == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "转换单聊消息失败");
        }
        // 设置消息id
        privateMessage.setId(privateMessageTxEvent.getEventId());
        // 设置消息发送人id
        privateMessage.setSendId(privateMessageTxEvent.getSendId());
        // 设置消息状态
        privateMessage.setStatus(MessageStatus.UNSEND.getCode());
        // 设置发送时间
        privateMessage.setSendTime(privateMessageTxEvent.getSendTime());
        // 保存数据
        return this.saveOrUpdate(privateMessage);
    }

    @Override
    public boolean checkExists(Long messageId) {
        return baseMapper.checkExists(messageId) != null;
    }

    @Override
    public List<PrivateMessage> getAllUnreadPrivateMessage(Long userId, List<Long> friendIds) {
        if (userId == null || CollectionUtil.isEmpty(friendIds)) {
            throw new BJException(HttpCode.PARAMS_ERROR, "获取所有未读私聊消息时，用户ID或好友列表不能为空");
        }
        // 获取当前用户所有未读消息
        LambdaQueryWrapper<PrivateMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateMessage::getRecvId, userId)
                .eq(PrivateMessage::getStatus, MessageStatus.UNSEND.getCode())
                .in(PrivateMessage::getSendId, friendIds);
        return this.list(queryWrapper);
    }

    @Override
    public List<PrivateMessageVO> getPrivateMessageVOList(Long userId, List<Long> friendIds) {
        return baseMapper.getPrivateMessageVOList(userId, friendIds);
    }

    @Override
    public List<PrivateMessageVO> loadMessage(Long userId, Long minId, Date minDate, List<Long> friendIds, int limitCount) {
        return baseMapper.loadMessage(userId, minId, minDate, friendIds, limitCount);
    }

    @Override
    public int batchUpdatePrivateMessageStatus(Integer status, List<Long> ids) {
        return baseMapper.batchUpdatePrivateMessageStatus(status, ids);
    }

    @Override
    public List<PrivateMessageVO> loadMessageByUserIdAndFriendId(Long userId, Long friendId, long stIdx, long size) {
        return baseMapper.loadMessageByUserIdAndFriendId(userId, friendId, stIdx, size);
    }

    @Override
    public int readedMessage(Long sendId, Long recvId) {
        return baseMapper.readedMessage(sendId, recvId);
    }

    @Override
    public int updateMessageStatusById(Integer status, Long messageId) {
        return baseMapper.updateMessageStatusById(status, messageId);
    }

    @Override
    public PrivateMessageVO getPrivateMessageById(Long messageId) {
        return baseMapper.getPrivateMessageById(messageId);
    }
}
