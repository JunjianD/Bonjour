package com.djj.bj.common.cache.local.factory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Grava本地缓存工厂类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.local.factory
 * @className GravaLocalCacheFactory
 * @date 2025/5/31 07:22
 */
public class GravaLocalCacheFactory {
    /**
     * 获取一个默认配置的本地缓存实例
     *
     * @param <K> 缓存的key类型
     * @param <V> 缓存的value类型
     * @return Cache<K, V> 本地缓存实例
     */
    public static <K,V> Cache<K,V> getLocalCache() {
        return CacheBuilder
                .newBuilder()
                .initialCapacity(200) // 初始容量
                .concurrencyLevel(5) // 并发级别
                .expireAfterWrite(300, TimeUnit.SECONDS) // 写入后300秒过期
                .build();
    }

    /**
     * 获取一个指定过期时间的本地缓存实例
     *
     * @param duration 缓存过期时间，单位为秒
     * @return Cache<K, V> 本地缓存实例
     * @param <K> 缓存的key类型
     * @param <V> 缓存的value类型
     */
    public static <K,V> Cache<K,V> getLocalCache(long duration) {
        return CacheBuilder
                .newBuilder()
                .initialCapacity(200)
                .concurrencyLevel(5)
                .expireAfterWrite(duration, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取一个指定初始容量和过期时间的本地缓存实例
     *
     * @param initialCapacity 初始容量
     * @param duration 缓存过期时间，单位为秒
     * @return Cache<K, V> 本地缓存实例
     * @param <K> 缓存的key类型
     * @param <V> 缓存的value类型
     */
    public static <K,V> Cache<K,V> getLocalCache(int initialCapacity, long duration) {
        return CacheBuilder
                .newBuilder()
                .initialCapacity(initialCapacity)
                .concurrencyLevel(5)
                .expireAfterWrite(duration, TimeUnit.SECONDS)
                .build();
    }
}
