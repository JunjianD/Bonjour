package com.djj.bj.common.cache.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.lock
 * @interfaceName DistributedLock
 * @date 2025/6/4 20:01
 */
public interface DistributedLock {
    /**
     * 尝试获取锁
     *
     * @param waitTime 等待时间
     * @param leaseTime 锁的持有时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     * @throws InterruptedException 中断异常
     */
    boolean tryLock(
            Long waitTime,
            Long leaseTime,
            TimeUnit unit
    ) throws InterruptedException;


    /**
     *  尝试获取锁
     * @param waitTime 等待时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     * @throws InterruptedException 中断异常
     */
    boolean tryLock(
            Long waitTime,
            TimeUnit unit
    )throws  InterruptedException;

    /**
     * 尝试获取锁
     * @return 是否成功获取锁
     * @throws InterruptedException 中断异常
     */
    boolean tryLock() throws InterruptedException;

    /**
     * 获取锁
     *
     * @param leaseTime 锁的持有时间
     * @param unit 时间单位
     */
    void lock(Long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     */
    void unlock();

    /**
     * 是否被锁定
     *
     * @return 是否被锁定
     */
    boolean isLocked();

    /**
     * 是否被指定线程持有
     *
     * @param threadId 线程ID
     * @return 是否被指定线程持有
     */
    boolean isHeldByThread(Long threadId);

    /**
     * 是否被当前线程持有
     *
     * @return 是否被当前线程持有
     */
    boolean isHeldByCurrentThread();

}
