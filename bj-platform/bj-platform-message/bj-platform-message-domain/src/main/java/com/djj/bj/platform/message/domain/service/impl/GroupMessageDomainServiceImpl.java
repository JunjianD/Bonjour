package com.djj.bj.platform.message.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.entity.GroupMessage;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.enums.MessageStatus;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;
import com.djj.bj.platform.message.domain.repository.GroupMessageRepository;
import com.djj.bj.platform.message.domain.service.GroupMessageDomainService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 群聊消息领域服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.service.impl
 * @className GroupMessageDomainServiceImpl
 * @date 2025/8/6 20:27
 */
@Service
public class GroupMessageDomainServiceImpl extends ServiceImpl<GroupMessageRepository, GroupMessage> implements GroupMessageDomainService {

    @Override
    public boolean saveGroupMessageTxEvent(GroupMessageTxEvent groupMessageTxEvent) {
        if (groupMessageTxEvent == null || groupMessageTxEvent.getGroupMessageDTO() == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群聊消息事务事件不能为空");
        }
        GroupMessage groupMessage = BeanUtils.copyProperties(groupMessageTxEvent.getGroupMessageDTO(), GroupMessage.class);
        if (groupMessage == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "转换群聊消息失败");
        }
        groupMessage.setId(groupMessageTxEvent.getEventId());
        groupMessage.setSendId(groupMessageTxEvent.getSendId());
        groupMessage.setSendNickName(groupMessageTxEvent.getSendNickName());
        groupMessage.setSendTime(groupMessageTxEvent.getSendTime());
        groupMessage.setStatus(MessageStatus.UNSEND.getCode());
        if (CollectionUtil.isNotEmpty(groupMessageTxEvent.getGroupMessageDTO().getAtUserIds())) {
            groupMessage.setAtUserIds(StrUtil.join(",", groupMessageTxEvent.getGroupMessageDTO().getAtUserIds()));
        }
        return this.saveOrUpdate(groupMessage);
    }

    @Override
    public boolean checkExists(Long messageId) {
        return baseMapper.checkExists(messageId) != null;
    }

    @Override
    public List<GroupMessageVO> getUnreadGroupMessageList(Long groupId, Date sendTime, Long sendId, Integer status, Long maxReadId, Integer limitCount) {
        return baseMapper.getUnreadGroupMessageList(groupId, sendTime, sendId, status, maxReadId, limitCount);
    }

    @Override
    public List<GroupMessageVO> loadGroupMessageList(Long minId, Date minDate, List<Long> ids, Integer status, Integer limitCount) {
        return baseMapper.loadGroupMessageList(minId, minDate, ids, status, limitCount);
    }

    @Override
    public List<GroupMessageVO> getHistoryMessage(Long groupId, Date sendTime, Integer status, long stIdx, long size) {
        return baseMapper.getHistoryMessage(groupId, sendTime, status, stIdx, size);
    }

    @Override
    public Long getMaxMessageId(Long groupId) {
        return baseMapper.getMaxMessageId(groupId);
    }

    @Override
    public GroupMessageVO getGroupMessageById(Long messageId) {
        return baseMapper.getGroupMessageById(messageId);
    }

    @Override
    public int updateStatus(Integer status, Long messageId) {
        return baseMapper.updateStatus(status, messageId);
    }
}
