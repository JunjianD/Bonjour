package com.djj.bj.common.cache.distribute.redis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.distribute.data.RedisData;
import com.djj.bj.common.cache.lock.DistributedLock;
import com.djj.bj.common.cache.lock.factory.DistributedLockFactory;
import com.djj.bj.common.cache.threadpool.ThreadPoolUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.events.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * 基于Redis的分布式缓存服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.distribute.redis
 * @className RedisDistributeCacheService
 * @date 2025/6/3 21:18
 */
@Component
@ConditionalOnProperty(name = "cache.type.distribute", havingValue = "redis")
public class RedisDistributeCacheService implements DistributeCacheService {
    private final Logger logger = LoggerFactory.getLogger(RedisDistributeCacheService.class);

    /**
     * 缓存空值的过期时间，单位为秒
     */
    private static final Long CACHE_NULL_TTL = 60L;

    /**
     * 缓存的空数据
     */
    private static final String EMPTY_VALUE = "";

    /**
     * 缓存空列表的值
     */
    private static final String EMPTY_LIST_VALUE = "[]";

    /**
     * 分布式锁后缀
     */
    private static final String LOCK_SUFFIX = "_lock";

    /**
     * 缓存的锁过期时间，单位为毫秒
     */
    private static final Long THREAD_SLEEP_MILLISECONDS = 50L;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DistributedLockFactory distributedLockFactory;


    @Override
    public void set(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, this.getValue(value));
    }

    @Override
    public Boolean expire(String key, Long timeout, TimeUnit timeUnit) {
        return stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    @Override
    public void set(String key, Object value, Long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, this.getValue(value), timeout, timeUnit);
    }

    @Override
    public void setWithLogicalExpire(String key, Object value, Long timeout, TimeUnit timeUnit) {
        RedisData redisData = new RedisData(value, LocalDateTime.now().plusSeconds(timeUnit.toSeconds(timeout)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        Object result = stringRedisTemplate.opsForValue().get(key);
        if (result == null) {
            return null;
        }
        try {
            return JSONUtil.toBean(result.toString(), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public List<String> multiGet(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Set<String> multiGetKeys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    @Override
    public Boolean delete(String key) {
        if (StrUtil.isEmpty(key)) {
            return false;
        }
        return stringRedisTemplate.delete(key);
    }

    @Override
    public <T, ID> T queryWithPassThrough(String keyPrefix, ID id, Class<T> clazz, Function<ID, T> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix, id); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResult(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        T t = dbFallback.apply(id); // 从数据库中获取数据
        if (t == null) {
            // 数据库中也没有数据，设置缓存空值，避免缓存穿透
            stringRedisTemplate.opsForValue().set(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
            return null;
        }
        // 数据库中有数据，设置缓存
        this.set(key, t, timeout, timeUnit);
        return t;
    }

    @Override
    public <T> T queryWithPassThroughWithoutArgs(String keyPrefix, Class<T> clazz, Supplier<T> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResult(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        T t = dbFallback.get(); // 从数据库中获取数据
        if (t == null) {
            // 数据库中也没有数据，设置缓存空值，避免缓存穿透
            stringRedisTemplate.opsForValue().set(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
            return null;
        }
        // 数据库中有数据，设置缓存
        this.set(key, t, timeout, timeUnit);
        return t;
    }

    @Override
    public <T, ID> List<T> queryWithPassThroughList(String keyPrefix, ID id, Class<T> clazz, Function<ID, List<T>> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix, id); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResultList(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        List<T> t = dbFallback.apply(id); // 从数据库中获取数据
        if (t == null) {
            // 数据库中也没有数据，设置缓存空值，避免缓存穿透
            stringRedisTemplate.opsForValue().set(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
            return null;
        }
        // 数据库中有数据，设置缓存
        this.set(key, t, timeout, timeUnit);
        return t;
    }

    @Override
    public <T> List<T> queryWithPassThroughListWithoutArgs(String keyPrefix, Class<T> clazz, Supplier<List<T>> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResultList(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        List<T> t = dbFallback.get(); // 从数据库中获取数据
        if (t == null) {
            // 数据库中也没有数据，设置缓存空值，避免缓存穿透
            stringRedisTemplate.opsForValue().set(key, EMPTY_LIST_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
            return null;
        }
        // 数据库中有数据，设置缓存
        this.set(key, t, timeout, timeUnit);
        return t;
    }

    @Override
    public <T, ID> T queryWithLogicalExpire(String keyPrefix, ID id, Class<T> clazz, Function<ID, T> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix, id); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isBlank(value)) {
            try {
                bulidCache(id, dbFallback, timeout, timeUnit, key);
                Thread.sleep(THREAD_SLEEP_MILLISECONDS); // 等待缓存构建完成
                return queryWithLogicalExpire(keyPrefix, id, clazz, dbFallback, timeout, timeUnit);
            } catch (InterruptedException e) {
                logger.error("query data with logical expire|{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        RedisData redisData = this.getResult(value, RedisData.class); // 反序列化缓存数据
        if (EMPTY_VALUE.equals(redisData.getData())) {
            return null; // 如果缓存中是空值，直接返回null
        }
        T t = this.getResult(redisData.getData(), clazz);
        // 判断缓存是否过期
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 缓存未过期，直接返回数据
            return t;
        }
        bulidCache(id, dbFallback, timeout, timeUnit, key); // 缓存过期，重新构建缓存
        return t;
    }


    @Override
    public <T> T queryWithLogicalExpireWithoutArgs(String keyPrefix, Class<T> clazz, Supplier<T> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isBlank(value)) {
            try {
                buildCacheWithoutArgs(dbFallback, timeout, timeUnit, key);
                Thread.sleep(THREAD_SLEEP_MILLISECONDS);
                return queryWithLogicalExpireWithoutArgs(keyPrefix, clazz, dbFallback, timeout, timeUnit);
            } catch (InterruptedException e) {
                logger.error("query data with logical expire|{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        // 反序列化缓存数据
        RedisData redisData = JSONUtil.toBean(value, RedisData.class);
        if(EMPTY_VALUE.equals(redisData.getData())){
            return null; // 如果缓存中是空值，直接返回null
        }
        T t = this.getResult(redisData.getData(), clazz);
        // 判断缓存是否过期
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 缓存未过期，直接返回数据
            return t;
        }
        buildCacheWithoutArgs(dbFallback, timeout, timeUnit, key);
        return t;
    }

    @Override
    public <T, ID> List<T> queryWithLogicalExpireList(String keyPrefix, ID id, Class<T> clazz, Function<ID, List<T>> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix, id); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isBlank(value)) {
            try {
                buildCacheList(id, dbFallback, timeout, timeUnit, key);
                Thread.sleep(THREAD_SLEEP_MILLISECONDS); // 等待缓存构建完成
                return queryWithLogicalExpireList(keyPrefix, id, clazz, dbFallback, timeout, timeUnit);
            } catch (InterruptedException e) {
                logger.error("query data with logical expire|{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        // 反序列化缓存数据
        RedisData redisData = this.getResult(value, RedisData.class);
        if (EMPTY_LIST_VALUE.equals(redisData.getData())) {
            return new ArrayList<>();
        }
        List<T> list = this.getResultList(JSONUtil.toJsonStr(redisData.getData()), clazz);
        // 判断缓存是否过期
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 缓存未过期，直接返回数据
            return list;
        }
        buildCacheList(id, dbFallback, timeout, timeUnit, key);
        return list;
    }

    @Override
    public <T> List<T> queryWithLogicalExpireListWithoutArgs(String keyPrefix, Class<T> clazz, Supplier<List<T>> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix);
        String str = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(str)) {
            try {
                buildCacheListWithoutArgs(dbFallback, timeout, timeUnit, key);
                Thread.sleep(THREAD_SLEEP_MILLISECONDS); // 等待缓存构建完成
                return queryWithLogicalExpireListWithoutArgs(keyPrefix, clazz, dbFallback, timeout, timeUnit);
            } catch (InterruptedException e) {
                logger.error("query data with logical expire|{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        // 反序列化缓存数据
        RedisData redisData = this.getResult(str, RedisData.class);
        if (EMPTY_LIST_VALUE.equals(redisData.getData())) {
            return new ArrayList<>();
        }
        List<T> list = this.getResultList(JSONUtil.toJsonStr(redisData.getData()), clazz);
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 缓存未过期，直接返回数据
            return list;
        }
        buildCacheListWithoutArgs(dbFallback, timeout, timeUnit, key);
        return list;
    }

    @Override
    public <T, ID> T queryWithMutex(String keyPrefix, ID id, Class<T> clazz, Function<ID, T> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix, id); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResult(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        String lockKey = this.getLockKey(key); // 获取锁key
        T t = null;
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try{
            boolean isLock = distributedLock.tryLock(); // 尝试获取锁
            if (!isLock) {
                Thread.sleep(THREAD_SLEEP_MILLISECONDS);
                return queryWithMutex(keyPrefix, id, clazz, dbFallback, timeout, timeUnit);
            }
            // 获取锁成功，继续查询缓存
            value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
            if (StrUtil.isNotBlank(value)) {
                // 如果缓存中有数据，直接返回
                return this.getResult(value, clazz);
            }
            t = dbFallback.apply(id); // 从数据库中获取数据
            if (t == null) {
                this.set(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS); // 数据库中也没有数据，设置缓存空值，避免缓存穿透
                return null;
            }
            this.set(key, t, timeout, timeUnit); // 数据库中有数据，设置缓存
        } catch (InterruptedException e) {
            logger.error("query data with mutex|{}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            distributedLock.unlock(); // 释放锁
        }
        return t;
    }

    @Override
    public <T> T queryWithMutexWithoutArgs(String keyPrefix, Class<T> clazz, Supplier<T> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResult(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        String lockKey = this.getLockKey(key); // 获取锁key
        T t = null;
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            boolean isLock = distributedLock.tryLock(); // 尝试获取锁
            if (!isLock) {
                Thread.sleep(THREAD_SLEEP_MILLISECONDS);
                return queryWithMutexWithoutArgs(keyPrefix, clazz, dbFallback, timeout, timeUnit);
            }
            // 获取锁成功，继续查询缓存
            value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
            if (StrUtil.isNotBlank(value)) {
                // 如果缓存中有数据，直接返回
                return this.getResult(value, clazz);
            }
            t = dbFallback.get(); // 从数据库中获取数据
            if (t == null) {
                this.set(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS); // 数据库中也没有数据，设置缓存空值，避免缓存穿透
                return null;
            }
            this.set(key, t, timeout, timeUnit); // 数据库中有数据，设置缓存
        } catch (InterruptedException e) {
            logger.error("query data with mutex|{}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            distributedLock.unlock(); // 释放锁
        }
        return t;
    }

    @Override
    public <T, ID> List<T> queryWithMutexList(String keyPrefix, ID id, Class<T> clazz, Function<ID, List<T>> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix, id);
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResultList(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        String lockKey = this.getLockKey(key); // 获取锁key
        List<T> t = null;
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            boolean isLock = distributedLock.tryLock(); // 尝试获取锁
            if (!isLock) {
                Thread.sleep(THREAD_SLEEP_MILLISECONDS);
                return queryWithMutexList(keyPrefix, id, clazz, dbFallback, timeout, timeUnit);
            }
            // 获取锁成功，继续查询缓存
            value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
            if (StrUtil.isNotBlank(value)) {
                // 如果缓存中有数据，直接返回
                return this.getResultList(value, clazz);
            }
            t = dbFallback.apply(id); // 从数据库中获取数据
            if (t == null) {
                this.set(key, EMPTY_LIST_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS); // 数据库中也没有数据，设置缓存空值，避免缓存穿透
                return null;
            }
            this.set(key, t, timeout, timeUnit); // 数据库中有数据，设置缓存
        } catch (InterruptedException e) {
            logger.error("query data with mutex list|{}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            distributedLock.unlock(); // 释放锁
        }
        return t;
    }

    @Override
    public <T> List<T> queryWithMutexListWithoutArgs(String keyPrefix, Class<T> clazz, Supplier<List<T>> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = this.getKey(keyPrefix); // 获取数据key
        String value = stringRedisTemplate.opsForValue().get(key); // 从缓存中获取数据
        if (StrUtil.isNotBlank(value)) {
            // 如果缓存中有数据，直接返回
            return this.getResultList(value, clazz);
        }
        // 缓存中为空串
        if (value != null) {
            return null;
        }
        String lockKey = this.getLockKey(key); // 获取锁key
        List<T> t = null;
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            boolean isLock = distributedLock.tryLock(); // 尝试获取锁
            if (!isLock) {
                Thread.sleep(THREAD_SLEEP_MILLISECONDS);
                return queryWithMutexListWithoutArgs(keyPrefix, clazz, dbFallback, timeout, timeUnit);
            }
            // 获取锁成功，继续查询缓存
            value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
            if (StrUtil.isNotBlank(value)) {
                // 如果缓存中有数据，直接返回
                return this.getResultList(value, clazz);
            }
            t = dbFallback.get(); // 从数据库中获取数据
            if (t == null) {
                this.set(key, EMPTY_LIST_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS); // 数据库中也没有数据，设置缓存空值，避免缓存穿透
                return null;
            }
            this.set(key, t, timeout, timeUnit); // 数据库中有数据，设置缓存
        } catch (InterruptedException e) {
            logger.error("query data with mutex list|{}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            distributedLock.unlock(); // 释放锁
        }
        return t;
    }

    private String getLockKey(String key) {
        return key.concat(LOCK_SUFFIX);
    }

    private <T, ID> void bulidCache(ID id, Function<ID, T> dbFallback, Long timeout, TimeUnit timeUnit, String key) {
        String lockKey = this.getLockKey(key);
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        ThreadPoolUtils.execute(() -> {
            try {
                boolean isLock = distributedLock.tryLock();
                if (isLock) {
                    T newT = null;
                    String value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
                    if (StrUtil.isEmpty(value)) {
                        newT = dbFallback.apply(id); // 从数据库中获取数据
                    } else {
                        RedisData redisData = this.getResult(value, RedisData.class);
                        LocalDateTime expireTime = redisData.getExpireTime();
                        if (expireTime.isBefore(LocalDateTime.now())) {
                            newT = dbFallback.apply(id);
                        }
                    }
                    if (newT != null) {
                        this.setWithLogicalExpire(key, newT, timeout, timeUnit);
                    } else {
                        this.setWithLogicalExpire(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("build cache | {}", e.getMessage());
                throw new RuntimeException(e);
            } finally {
                distributedLock.unlock(); // 释放锁
            }
        });
    }

    private <T> void buildCacheWithoutArgs(Supplier<T> dbFallback, Long timeout, TimeUnit timeUnit, String key) {
        String lockKey = this.getLockKey(key);
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        ThreadPoolUtils.execute(() -> {
            try {
                boolean isLock = distributedLock.tryLock();
                if (isLock) {
                    T newT = null;
                    String value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
                    if (StrUtil.isEmpty(value)) {
                        newT = dbFallback.get(); // 从数据库中获取数据
                    } else {
                        RedisData redisData = this.getResult(value, RedisData.class);
                        LocalDateTime expireTime = redisData.getExpireTime();
                        if (expireTime.isBefore(LocalDateTime.now())) {
                            newT = dbFallback.get();
                        }
                    }
                    if (newT != null) {
                        this.setWithLogicalExpire(key, newT, timeout, timeUnit);
                    } else {
                        this.setWithLogicalExpire(key, EMPTY_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("build cache | {}", e.getMessage());
                throw new RuntimeException(e);
            } finally {
                distributedLock.unlock(); // 释放锁
            }
        });
    }

    private <T, ID> void buildCacheList(ID id, Function<ID, List<T>> dbFallback, Long timeout, TimeUnit timeUnit, String key) {
        String lockKey = this.getLockKey(key);
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        ThreadPoolUtils.execute(() -> {
            try {
                boolean isLock = distributedLock.tryLock();
                if (isLock) {
                    List<T> newT = null;
                    String value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
                    if (StrUtil.isEmpty(value)) {
                        newT = dbFallback.apply(id); // 从数据库中获取数据
                    } else {
                        RedisData redisData = this.getResult(value, RedisData.class);
                        LocalDateTime expireTime = redisData.getExpireTime();
                        if (expireTime.isBefore(LocalDateTime.now())) {
                            newT = dbFallback.apply(id);
                        }
                    }
                    if (newT != null) {
                        this.setWithLogicalExpire(key, newT, timeout, timeUnit);
                    } else {
                        this.setWithLogicalExpire(key, EMPTY_LIST_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("build cache | {}", e.getMessage());
                throw new RuntimeException(e);
            } finally {
                distributedLock.unlock(); // 释放锁
            }
        });
    }

    private <T> void buildCacheListWithoutArgs(Supplier<List<T>> dbFallback, Long timeout, TimeUnit timeUnit, String key) {
        String lockKey = this.getLockKey(key);
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        ThreadPoolUtils.execute(() -> {
            try {
                boolean isLock = distributedLock.tryLock();
                if (isLock) {
                    List<T> newT = null;
                    String value = stringRedisTemplate.opsForValue().get(key); // 再次检查缓存
                    if (StrUtil.isEmpty(value)) {
                        newT = dbFallback.get(); // 从数据库中获取数据
                    } else {
                        RedisData redisData = this.getResult(value, RedisData.class);
                        LocalDateTime expireTime = redisData.getExpireTime();
                        if (expireTime.isBefore(LocalDateTime.now())) {
                            newT = dbFallback.get();
                        }
                    }
                    if (newT != null) {
                        this.setWithLogicalExpire(key, newT, timeout, timeUnit);
                    } else {
                        this.setWithLogicalExpire(key, EMPTY_LIST_VALUE, CACHE_NULL_TTL, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("build cache | {}", e.getMessage());
                throw new RuntimeException(e);
            } finally {
                distributedLock.unlock(); // 释放锁
            }
        });
    }
}
