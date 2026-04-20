package com.djj.bj.platform.friend.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
            this.evictFriendCache(userId, friendId);
            domainService.publishEvent(userId, friendId, PlatformConstants.FRIEND_HANDLER_BIND);
        }
        if (BooleanUtil.isTrue(results[1])) {
            this.evictFriendCache(friendId, userId);
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
            this.evictFriendCache(userId, friendId);
            domainService.publishEvent(userId, friendId, PlatformConstants.FRIEND_HANDLER_UNBIND);
        }
        if (BooleanUtil.isTrue(results[1])) {
            this.evictFriendCache(friendId, userId);
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
            this.evictFriendCache(userId, vo.getId());
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
        if (friendId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "更新好友资料时，好友ID不能为空");
        }
        if (StrUtil.isAllEmpty(headImage, nickName)) {
            logger.info("FriendServiceImpl.updateFriendByFriendId|头像和昵称均为空，跳过更新, friendId:{}", friendId);
            return false;
        }
        boolean updated = domainService.updateFriendByFriendId(headImage, nickName, friendId);
        if (!updated) {
            logger.warn("FriendServiceImpl.updateFriendByFriendId|好友资料未更新，friendId:{}", friendId);
            return false;
        }
        List<Long> userIdList = domainService.getUserIdListByFriendId(friendId);
        if (CollectionUtil.isEmpty(userIdList)) {
            logger.info("FriendServiceImpl.updateFriendByFriendId|未找到需要刷新的好友缓存, friendId:{}", friendId);
            return true;
        }
        userIdList.forEach(userId -> this.evictFriendViewCache(userId, friendId));
        logger.info("FriendServiceImpl.updateFriendByFriendId|好友资料同步完成并清理缓存, friendId:{}, userCount:{}", friendId, userIdList.size());
        return true;
    }

    private void evictFriendCache(Long userId, Long friendId) {
        if (userId == null) {
            return;
        }
        distributeCacheService.delete(distributeCacheService.getKey(
                PlatformConstants.PLATFORM_REDIS_FRIEND_LIST_KEY,
                userId
        ));
        distributeCacheService.delete(distributeCacheService.getKey(
                PlatformConstants.PLATFORM_REDIS_FRIEND_SET_KEY,
                userId
        ));
        if (friendId != null) {
            distributeCacheService.delete(distributeCacheService.getKey(
                    PlatformConstants.PLATFORM_REDIS_FRIEND_SINGLE_KEY,
                    new FriendCommand(userId, friendId)
            ));
        }
        logger.info("FriendServiceImpl.evictFriendCache|好友缓存已删除, userId:{}, friendId:{}", userId, friendId);
    }

    private void evictFriendViewCache(Long userId, Long friendId) {
        if (userId == null) {
            return;
        }
        distributeCacheService.delete(distributeCacheService.getKey(
                PlatformConstants.PLATFORM_REDIS_FRIEND_LIST_KEY,
                userId
        ));
        if (friendId != null) {
            distributeCacheService.delete(distributeCacheService.getKey(
                    PlatformConstants.PLATFORM_REDIS_FRIEND_SINGLE_KEY,
                    new FriendCommand(userId, friendId)
            ));
        }
        logger.info("FriendServiceImpl.evictFriendViewCache|好友视图缓存已删除, userId:{}, friendId:{}", userId, friendId);
    }
}
