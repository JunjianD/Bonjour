package com.djj.bj.platform.friend.application.cache.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.lock.DistributedLock;
import com.djj.bj.common.cache.lock.factory.DistributedLockFactory;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.vo.FriendVO;
import com.djj.bj.platform.friend.application.cache.FriendCacheService;
import com.djj.bj.platform.friend.domain.event.FriendEvent;
import com.djj.bj.platform.friend.domain.model.command.FriendCommand;
import com.djj.bj.platform.friend.domain.service.FriendDomainService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 好友缓存服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.cache.impl
 * @className FriendCacheServiceImpl
 * @date 2025/8/2 15:47
 */
@Service
public class FriendCacheServiceImpl implements FriendCacheService {
    private final Logger logger = LoggerFactory.getLogger(FriendCacheServiceImpl.class);

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private FriendDomainService friendDomainService;

    @Resource
    private DistributedLockFactory distributeLockFactory;

    @Override
    public void updateFriendCache(FriendEvent friendEvent) {
        if (friendEvent == null || friendEvent.getEventId() == null) {
            logger.info("IMFriendCacheService|更新分布式缓存时，参数为空");
            return;
        }
        DistributedLock distributedLock = distributeLockFactory.getDistributedLock(PlatformConstants.getKey(
                PlatformConstants.FRIEND_UPDATE_CACHE_LOCK_KEY,
                String.valueOf(friendEvent.getEventId())
        ));
        try {
            boolean success = distributedLock.tryLock();
            if (!success) {
                logger.info("IMFriendCacheService|更新分布式缓存时，获取锁失败，key:{}", PlatformConstants.FRIEND_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(friendEvent.getEventId())));
                return;
            }
            switch (friendEvent.getHandler()) {
                case PlatformConstants.FRIEND_HANDLER_BIND -> this.bindFriend(friendEvent);
                case PlatformConstants.FRIEND_HANDLER_UNBIND -> this.unbindFriend(friendEvent);
                case PlatformConstants.FRIEND_HANDLER_UPDATE -> this.updateFriend(friendEvent);
                default -> {
                    logger.info("IMFriendCacheService|更新分布式缓存时，操作类型不匹配，handler:{}, 使用默认更新", friendEvent.getHandler());
                    this.updateFriend(friendEvent);
                }
            }
        } catch (Exception e) {
            logger.error("IMFriendCacheService|更新分布式缓存时发生异常 | {}", JSONObject.toJSONString(friendEvent));
        } finally {
            distributedLock.unlock();
        }
    }

    private void bindFriend(FriendEvent friendEvent) {
        String redisKey = "";
        if (friendEvent.getFriendId() != null) {
            redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_SET_KEY, friendEvent.getEventId());
            distributeCacheService.addSet(redisKey, String.valueOf(friendEvent.getFriendId()));
        }
        // 获取好友列表
        List<Friend> friendList = friendDomainService.getFriendByUserId(friendEvent.getEventId());
        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_LIST_KEY, friendEvent.getEventId());
        if (!CollectionUtil.isEmpty(friendList)) {
            distributeCacheService.set(redisKey, friendList, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        FriendCommand friendCommand = new FriendCommand(friendEvent.getEventId(), friendEvent.getFriendId());
        FriendVO friendVO = friendDomainService.findFriend(friendCommand);
        if (friendVO != null) {
            redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_SINGLE_KEY, friendCommand);
            distributeCacheService.set(redisKey, friendVO, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
    }

    private void unbindFriend(FriendEvent friendEvent) {
        String redisKey = "";
        if (friendEvent.getFriendId() != null) {
            redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_SET_KEY, friendEvent.getEventId());
            distributeCacheService.removeSet(redisKey, String.valueOf(friendEvent.getFriendId()));
        }
        // 获取好友列表
        List<Friend> friendList = friendDomainService.getFriendByUserId(friendEvent.getEventId());
        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_LIST_KEY, friendEvent.getEventId());
        if (!CollectionUtil.isEmpty(friendList)) {
            distributeCacheService.set(redisKey, friendList, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        FriendCommand friendCommand = new FriendCommand(friendEvent.getEventId(), friendEvent.getFriendId());
        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_SINGLE_KEY, friendCommand);
        distributeCacheService.delete(redisKey);
    }

    private void updateFriend(FriendEvent friendEvent) {
        String redisKey = "";
        List<Friend> friendList = friendDomainService.getFriendByUserId(friendEvent.getEventId());
        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_LIST_KEY, friendEvent.getEventId());
        if (!CollectionUtil.isEmpty(friendList)) {
            distributeCacheService.set(redisKey, friendList, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        FriendCommand friendCommand = new FriendCommand(friendEvent.getEventId(), friendEvent.getFriendId());
        FriendVO friendVO = friendDomainService.findFriend(friendCommand);
        if (friendVO != null) {
            redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_FRIEND_SINGLE_KEY, friendCommand);
            distributeCacheService.set(redisKey, friendVO, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
    }
}
