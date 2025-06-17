package com.djj.bj.common.cache.local.impl;

import com.djj.bj.common.cache.local.LocalCacheService;
import com.djj.bj.common.cache.local.factory.GravaLocalCacheFactory;
import com.google.common.cache.Cache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 基于Guava实现的本地缓存
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.local.impl
 * @className GuavaLocalCacheService
 * @date 2025/5/31 06:51
 */
@Component
@ConditionalOnProperty(name = "cache.type.local", havingValue = "guava")
public class GuavaLocalCacheService<K,V> implements LocalCacheService<K,V> {
    // 本地缓存，基于Guava实现
    private final Cache<K, V> cache = GravaLocalCacheFactory.getLocalCache();


    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }
}
