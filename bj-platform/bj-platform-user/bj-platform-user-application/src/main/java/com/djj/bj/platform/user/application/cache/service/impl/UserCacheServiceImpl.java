package com.djj.bj.platform.user.application.cache.service.impl;

import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.lock.DistributedLock;
import com.djj.bj.common.cache.lock.factory.DistributedLockFactory;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.user.application.cache.service.UserCacheService;
import com.djj.bj.platform.user.domain.repository.UserRepository;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户缓存服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.cache.service.impl
 * @className UserCacheServiceImpl
 * @date 2025/7/25 18:13
 */
@Service
public class UserCacheServiceImpl implements UserCacheService {
    private final Logger logger = LoggerFactory.getLogger(UserCacheServiceImpl.class);

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private DistributedLockFactory distributeLockFactory;


    @Override
    public void updateUserCache(Long userId) {
        if (userId == null) {
            logger.info("UserCacheServiceImpl.updateUserCache|用户ID为空，无法更新缓存");
            return;
        }
        logger.info("UserCacheServiceImpl.updateUserCache|开始更新用户缓存，userId:{}", userId);
        // 获取分布式锁
        DistributedLock lock = distributeLockFactory.getDistributedLock(PlatformConstants.getKey(
                PlatformConstants.USER_UPDATE_CACHE_LOCK_KEY,
                userId.toString()
        ));
        try {
            boolean isSuccess = lock.tryLock();
            if (!isSuccess) {
                logger.info("UserCacheServiceImpl.updateUserCache|线程{}获取分布式锁失败，无法更新缓存 userId:{}", Thread.currentThread().getName(), userId);
                return;
            }
            logger.info("UserCacheServiceImpl.updateUserCache|线程{}获取分布式锁成功，开始更新缓存 userId:{}", Thread.currentThread().getName(), userId);
            User user = userRepository.selectById(userId);
            if (user == null) {
                logger.info("UserCacheServiceImpl.updateUserCache|未找到用户信息，无法更新缓存 userId:{}", userId);
                return;
            }
            // 更新用户id的缓存
            String userIdKey = distributeCacheService.getKey(
                    PlatformConstants.PLATFORM_REDIS_USER_KEY,
                    userId
            );

            distributeCacheService.set(userIdKey, user, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

            // 更新用户名称的缓存
            String userNameKey = distributeCacheService.getKey(
                    PlatformConstants.PLATFORM_REDIS_USER_KEY,
                    user.getUserName()
            );
            distributeCacheService.set(userNameKey, user, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            logger.info("UserCacheServiceImpl.updateUserCache|用户缓存更新成功，userId:{}", userId);
        } catch (Exception e) {
            logger.error("UserCacheServiceImpl.updateUserCache|获取分布式锁异常，更新缓存失败 userId:{}", userId, e);
        } finally {
            lock.unlock();
        }
    }
}
