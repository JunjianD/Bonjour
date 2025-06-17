package com.djj.bj.common.cache.lock.redisson;

import com.djj.bj.common.cache.lock.DistributedLock;
import com.djj.bj.common.cache.lock.factory.DistributedLockFactory;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的分布式锁实现
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.lock.redisson
 * @className RedissonLockFactory
 * @date 2025/6/4 20:09
 */
@Component
@ConditionalOnProperty(name="distribute.type.lock", havingValue = "redisson")
public class RedissonLockFactory implements DistributedLockFactory {
    private final Logger logger = LoggerFactory.getLogger(RedissonLockFactory.class);

    @Resource
    private RedissonClient redissonClient;

    @Override
    public DistributedLock getDistributedLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        return new DistributedLock() {
            @Override
            public boolean tryLock(Long waitTime, Long leaseTime, TimeUnit unit) throws InterruptedException {
                boolean isLockSuccess = rLock.tryLock(waitTime, leaseTime, unit);
                logger.info("{} get lock result:{}", key, isLockSuccess);
                return isLockSuccess;
            }

            @Override
            public boolean tryLock(Long waitTime, TimeUnit unit) throws InterruptedException {
                return rLock.tryLock(waitTime, unit);
            }

            @Override
            public boolean tryLock() throws InterruptedException {
                return rLock.tryLock();
            }

            @Override
            public void lock(Long leaseTime, TimeUnit unit) {
                rLock.lock(leaseTime, unit);
            }

            @Override
            public void unlock() {
                if(isLocked() && isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            }

            @Override
            public boolean isLocked() {
                return rLock.isLocked();
            }

            @Override
            public boolean isHeldByThread(Long threadId) {
                return rLock.isHeldByThread(threadId);
            }

            @Override
            public boolean isHeldByCurrentThread() {
                return rLock.isHeldByCurrentThread();
            }
        };
    }

}
