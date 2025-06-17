package com.djj.bj.common.cache.local;

/**
 * 本地缓存接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.local
 * @interfaceName LocalCacheService
 * @date 2025/5/31 06:44
 */
public interface LocalCacheService<K,V> {

    /**
     * 向缓存中添加数据
     *
     * @param key   缓存的key
     * @param value 缓存的value
     */
    void put(K key, V value);

    /**
     * 根据key从缓存中查询数据
     *
     * @param key 缓存的key
     * @return 缓存的value值
     */
    V getIfPresent(Object key);

    /**
     * 移除缓存中的数据
     *
     * @param key 缓存的key
     */
    void remove(K key);

}
