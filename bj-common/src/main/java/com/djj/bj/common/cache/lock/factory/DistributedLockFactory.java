package com.djj.bj.common.cache.lock.factory;

import com.djj.bj.common.cache.lock.DistributedLock;

/**
 * 分布式锁工厂接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.lock.factory
 * @interfaceName DistributedLockFactory
 * @date 2025/6/4 20:07
 */
public interface DistributedLockFactory {
    /**
     * 根据锁key获取分布式锁
     *
     * @param key 锁的唯一标识
     * @return 分布式锁实例
     */
    DistributedLock getDistributedLock(String key);
}
