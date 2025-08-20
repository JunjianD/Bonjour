package com.djj.bj.platform.friend.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.mq.event.MessageEventSenderService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.entity.User;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public Boolean[] bindFriend(Long userId, User user, Long friendId, User friend) {
        if (userId == null || friendId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        boolean result1 = false, result2 = false;
        Integer checkStatus = baseMapper.checkFriend(friendId, userId);
        if (checkStatus == null) {
            Friend friendship = new Friend();
            friendship.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
            friendship.setUserId(userId);
            friendship.setFriendId(friendId);
            friendship.setFriendHeadImage(friend == null ? "" : friend.getHeadImage());
            friendship.setFriendNickName(friend == null ? "" : friend.getNickName());
            result1 = this.save(friendship);
        }
        checkStatus = baseMapper.checkFriend(userId, friendId);
        if (checkStatus == null) {
            Friend friendship = new Friend();
            friendship.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
            friendship.setUserId(friendId);
            friendship.setFriendId(userId);
            friendship.setFriendHeadImage(user == null ? "" : user.getHeadImage());
            friendship.setFriendNickName(user == null ? "" : user.getNickName());
            result2 = this.save(friendship);
        }
        return new Boolean[]{result1, result2};
    }

    @Override
    public void publishEvent(Long userId, Long friendId, String eventType) {
        FriendEvent friendEvent = new FriendEvent(
                userId,
                friendId,
                eventType,
                this.getTopicEvent()
        );
        messageEventSenderService.send(friendEvent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean[] unbindFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        boolean result1, result2;
        result1 = baseMapper.deleteFriend(friendId, userId) > 0;
        result2 = baseMapper.deleteFriend(userId, friendId) > 0;
        return new Boolean[]{result1, result2};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(FriendVO vo, Long userId) {
        return baseMapper.updateFriend(vo.getHeadImage(), vo.getNickName(), vo.getId(), userId) > 0;
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
