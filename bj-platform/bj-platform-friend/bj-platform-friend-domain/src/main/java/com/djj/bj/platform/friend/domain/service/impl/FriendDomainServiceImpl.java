package com.djj.bj.platform.friend.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.mq.event.MessageEventSenderService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.vo.FriendVO;
import com.djj.bj.platform.friend.domain.event.FriendEvent;
import com.djj.bj.platform.friend.domain.model.command.FriendCommand;
import com.djj.bj.platform.friend.domain.repository.FriendRepository;
import com.djj.bj.platform.friend.domain.service.FriendDomainService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 好友领域服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.domain.service.impl
 * @className FriendDomainServiceImpl
 * @date 2025/8/1 15:17
 */
@Service
public class FriendDomainServiceImpl extends ServiceImpl<FriendRepository, Friend> implements FriendDomainService {
    private final Logger logger = LoggerFactory.getLogger(FriendDomainServiceImpl.class);

    @Resource
    private MessageEventSenderService messageEventSenderService;

    @Value("${message.mq.event.type}")
    private String eventType;

    @Override
    public List<Long> getFriendIdList(Long userId) {
        return baseMapper.getFriendIdList(userId);
    }

    @Override
    public List<FriendVO> findFriendByUserId(Long userId) {
        if (userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getFriendVOList(userId);
    }

    @Override
    public List<Friend> getFriendByUserId(Long userId) {
        return baseMapper.getFriendByUserId(userId);
    }

    @Override
    public Boolean isFriend(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.checkFriend(userId2, userId1) != null;
    }

    @Override
    public void bindFriend(FriendCommand friendCommand, String headImg, String nickName) {
        if (friendCommand == null || friendCommand.isEmpty()) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        boolean result = false;
        Integer checkStatus = baseMapper.checkFriend(friendCommand.getFriendId(), friendCommand.getUserId());
        if (checkStatus == null) {
            Friend friend = new Friend();
            friend.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
            friend.setUserId(friendCommand.getUserId());
            friend.setFriendId(friendCommand.getFriendId());
            friend.setFriendHeadImage(headImg);
            friend.setFriendNickName(nickName);
            result = this.save(friend);
        }
        if (result) {
            FriendEvent friendEvent = new FriendEvent(
                    friendCommand.getUserId(),
                    friendCommand.getFriendId(),
                    PlatformConstants.FRIEND_HANDLER_BIND,
                    this.getTopicEvent()
            );
            messageEventSenderService.send(friendEvent);
        }
    }

    @Override
    public void unbindFriend(FriendCommand friendCommand) {
        if (friendCommand == null || friendCommand.isEmpty()) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        int count = baseMapper.deleteFriend(friendCommand.getFriendId(), friendCommand.getUserId());
        if (count > 0) {
            FriendEvent friendEvent = new FriendEvent(
                    friendCommand.getUserId(),
                    friendCommand.getFriendId(),
                    PlatformConstants.FRIEND_HANDLER_UNBIND,
                    this.getTopicEvent()
            );
            messageEventSenderService.send(friendEvent);
        }
    }

    @Override
    public void update(FriendVO vo, Long userId) {
        int count = baseMapper.updateFriend(vo.getHeadImage(), vo.getNickName(), vo.getId(), userId);
        if (count > 0) {
            FriendEvent friendEvent = new FriendEvent(
                    userId,
                    vo.getId(),
                    PlatformConstants.FRIEND_HANDLER_UPDATE,
                    this.getTopicEvent()
            );
            messageEventSenderService.send(friendEvent);
        }
    }

    @Override
    public FriendVO findFriend(FriendCommand friendCommand) {
        if (friendCommand == null || friendCommand.isEmpty()) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getFriendVO(friendCommand.getFriendId(), friendCommand.getUserId());
    }

    @Override
    public boolean updateFriendByFriendId(String headImage, String nickName, Long friendId) {
        return baseMapper.updateFriendByFriendId(headImage, nickName, friendId) > 0;
    }

    /**
     * 获取事件发布主题
     *
     * @return 事件发布主题
     */
    private String getTopicEvent() {
        return PlatformConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ?
                PlatformConstants.TOPIC_EVENT_ROCKETMQ_FRIEND :
                PlatformConstants.TOPIC_EVENT_LOCAL;
    }
}
