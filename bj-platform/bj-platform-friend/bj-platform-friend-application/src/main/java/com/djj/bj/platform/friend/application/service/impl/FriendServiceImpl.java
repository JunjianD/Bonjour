package com.djj.bj.platform.friend.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.vo.FriendVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.dubbo.user.UserDubboService;
import com.djj.bj.platform.friend.application.service.FriendService;
import com.djj.bj.platform.friend.domain.model.command.FriendCommand;
import com.djj.bj.platform.friend.domain.service.FriendDomainService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 好友应用层服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.service.impl
 * @className FriendServiceImpl
 * @date 2025/8/1 15:49
 */
@Service
public class FriendServiceImpl implements FriendService {
    private final Logger logger = LoggerFactory.getLogger(FriendServiceImpl.class);

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private FriendDomainService domainService;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false)
    private UserDubboService userDubboService;

    @Override
    public List<Long> getFriendIdList(Long userId) {
        if (userId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "查询好友ID列表时，用户ID不能为空");
        }
        List<Friend> friendList = this.getFriendByUserId(userId);
        if (CollectionUtil.isEmpty(friendList)) {
            return Collections.emptyList();
        }
        return friendList.stream().map(Friend::getFriendId).toList();
    }

    @Override
    public Boolean isFriend(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "查询好友关系时，用户ID不能为空");
        }

        String redisKey = PlatformConstants.getKey(
                PlatformConstants.PLATFORM_REDIS_FRIEND_SET_KEY,
                String.valueOf(userId1)
        );
        Boolean result = distributeCacheService.isMemberSet(redisKey, userId2);
        if (BooleanUtil.isTrue(result)) {
            return result;
        }
        result = domainService.isFriend(userId1, userId2);
        if (BooleanUtil.isTrue(result)) {
            distributeCacheService.addSet(redisKey, String.valueOf(userId2));
        }
        return result;
    }

    @Override
    public List<FriendVO> findFriendByUserId(Long userId) {
        if (userId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "用户ID不能为空");
        }
        List<Friend> friendList = this.getFriendByUserId(userId);
        if (CollectionUtil.isEmpty(friendList)) {
            return Collections.emptyList();
        }
        return friendList.stream().map(
                friend -> new FriendVO(
                        friend.getFriendId(),
                        friend.getFriendNickName(),
                        friend.getFriendHeadImage()
                )
        ).toList();
    }

    @Override
    public List<Friend> getFriendByUserId(Long userId) {
        if (userId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "用户ID不能为空");
        }
        return distributeCacheService.queryWithPassThroughList(
                PlatformConstants.PLATFORM_REDIS_FRIEND_LIST_KEY,
                userId,
                Friend.class,
                domainService::getFriendByUserId,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void addFriend(Long friendId) {
        if (friendId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "添加好友时，好友ID不能为空");
        }
        Long userId = SessionContext.getUserSession().getUserId();
        if (Objects.equals(userId, friendId)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "不允许添加自己为好友");
        }
        User user = userDubboService.getUserById(userId);
        User friend = userDubboService.getUserById(friendId);
        Boolean[] results = domainService.bindFriend(userId, user, friendId, friend);
        if (BooleanUtil.isTrue(results[0])) {
            domainService.publishEvent(userId, friendId, PlatformConstants.FRIEND_HANDLER_BIND);
        }
        if (BooleanUtil.isTrue(results[1])) {
            domainService.publishEvent(friendId, userId, PlatformConstants.FRIEND_HANDLER_BIND);
        }
    }

    @Override
    public void delFriend(Long friendId) {
        if (friendId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "删除好友时，好友ID不能为空");
        }
        Long userId = SessionContext.getUserSession().getUserId();
        if (Objects.equals(userId, friendId)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "自己无法删除自己");
        }
        Boolean[] results = domainService.unbindFriend(userId, friendId);
        if (BooleanUtil.isTrue(results[0])) {
            domainService.publishEvent(userId, friendId, PlatformConstants.FRIEND_HANDLER_UNBIND);
        }
        if (BooleanUtil.isTrue(results[1])) {
            domainService.publishEvent(friendId, userId, PlatformConstants.FRIEND_HANDLER_UNBIND);
        }
    }

    @Override
    public void update(FriendVO vo) {
        if (vo == null || vo.getId() == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR);
        }
        Long userId = SessionContext.getUserSession().getUserId();
        Boolean result = domainService.update(vo, userId);
        if (BooleanUtil.isTrue(result)) {
            domainService.publishEvent(userId, vo.getId(), PlatformConstants.FRIEND_HANDLER_UPDATE);
        }
    }

    @Override
    public FriendVO findFriend(Long friendId) {
        if (friendId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "查询好友信息时，好友ID不能为空");
        }
        return distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_FRIEND_SINGLE_KEY,
                new FriendCommand(SessionContext.getUserSession().getUserId(), friendId),
                FriendVO.class,
                domainService::findFriend,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public boolean updateFriendByFriendId(String headImage, String nickName, Long friendId) {
        return domainService.updateFriendByFriendId(headImage, nickName, friendId);
    }
}
