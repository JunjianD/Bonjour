package com.djj.bj.common.cache.distribute;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.djj.bj.common.cache.distribute.conversion.TypeConversion;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分布式缓存接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.distribute
 * @interfaceName DistributeCacheService
 * @date 2025/5/31 06:44
 */
public interface DistributeCacheService {
    /**
     * 永久缓存
     *
     * @param key 缓存的key
     * @param value 缓存的value
     */
    void set(String key, Object value);

    /**
     * 设置缓存过期
     *
     * @param key 缓存的key
     * @param timeout 过期时长
     * @param timeUnit 时间单位
     * @return 设置过期时间，返回是否成功
     */
    Boolean expire(String key, final Long timeout, final TimeUnit timeUnit);

    /**
     * 将数据缓存一段时间，失效自动删除
     *
     * @param key 缓存的key
     * @param value 缓存的value
     * @param timeout 物理缓存的时长
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, Long timeout, TimeUnit timeUnit);

    /**
     * 保存缓存时设置逻辑过期时间，自行判断是否失效
     *
     * @param key 缓存的key
     * @param value 缓存的value
     * @param timeout 缓存逻辑过期时长
     * @param timeUnit 缓存逻辑时间单位
     */
    void setWithLogicalExpire(String key, Object value, Long timeout, TimeUnit timeUnit);

    /**
     * 获取缓存中的数据
     *
     * @param key 缓存的key
     * @param <T> 缓存的value类型
     * @return 缓存的value
     */
    <T> T getObject(String key, Class<T> clazz);

    /**
     * 获取缓存中的数据
     *
     * @param key 缓存的key
     * @return 缓存的value
     */
    String get(String key);

    /**
     * 批量获取缓存数据
     *
     * @param keys key列表
     * @return value集合
     */
    List<String> multiGet(Collection<String> keys);

    /**
     * 根据正则表达式获取所有的key集合
     *
     * @param pattern 正则表达式
     * @return key集合
     */
    Set<String> multiGetKeys(String pattern);

    /**
     * 删除缓存
     *
     * @param key 缓存的key
     * @return 删除是否成功
     */
    Boolean delete(String key);


    /**
     * 查询缓存，如果缓存不存在则通过dbFallback函数从数据库获取数据，并将结果缓存，防止缓存穿透
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 缓存的业务标识，通常是数据库主键
     * @param clazz 缓存的值的类型
     * @param dbFallback 查询数据库的Function函数，接收id参数并返回查询结果
     * @param timeout 缓存的过期时间
     * @param timeUnit 缓存的过期时间单位
     * @return 查询到的对象，如果缓存不存在则通过dbFallback函数从数据库获取数据并缓存
     * @param <T> 缓存的值的泛型
     * @param <ID> 查询数据库参数的泛型，也是参数泛型类型
     */
    <T,ID> T queryWithPassThrough(
            String keyPrefix,
            ID id,
            Class<T> clazz,
            Function<ID,T> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 不带参数查询对象和简单类型数据，防止缓存穿透
     *
     * @param keyPrefix key的前缀
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 无参数查询数据库数据
     * @param timeout 缓存的时长
     * @param timeUnit 时间单位
     * @return 返回业务数据
     * @param <T> 结果泛型
     */
    <T> T queryWithPassThroughWithoutArgs(
            String keyPrefix,
            Class<T> clazz,
            Supplier<T> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 带参数查询集合数据，防止缓存穿透
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 缓存的业务标识，通常是数据库主键
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 查询数据库的Function函数，接收id参数并返回查询结果
     * @param timeout 缓存的时长
     * @param timeUnit 时间单位
     * @return 返回业务数据
     * @param <T> 结果泛型
     * @param <ID> 查询数据库参数泛型，也是参数泛型类型
     */
    <T,ID> List<T> queryWithPassThroughList(
            String keyPrefix,
            ID id,
            Class<T> clazz,
            Function<ID,List<T>> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 不带参数查询集合数据，防止缓存穿透
     *
     * @param keyPrefix 缓存key的前缀
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 无参数查询数据库数据
     * @param timeout 缓存的时长
     * @param timeUnit 时间单位
     * @return 返回业务数据
     * @param <T> 结果泛型
     */
    <T> List<T> queryWithPassThroughListWithoutArgs(
            String keyPrefix,
            Class<T> clazz,
            Supplier<List<T>> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 带参数查询数据，按照逻辑过期时间读取缓存数据，新开线程重建缓存，其他线程直接返回逻辑过期数据，不占用资源
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 缓存业务标识，也是查询数据库的参数
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 查询数据库的Function函数，接收id参数并返回查询结果
     * @param timeout 缓存逻辑过期时长
     * @param timeUnit 缓存逻辑过期时间单位
     * @return 业务数据
     * @param <T> 结果泛型
     * @param <ID> 查询数据库参数泛型，也是参数泛型类型
     */
    <T,ID> T queryWithLogicalExpire(
            String keyPrefix,
            ID id,
            Class<T> clazz,
            Function<ID,T> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 不带参数查询数据，按照逻辑过期时间读取缓存数据，新开线程重建缓存，其他线程直接返回逻辑过期数据，不占用资源
     *
     * @param keyPrefix 缓存key的前缀
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 无参数查询数据库数据
     * @param timeout 缓存的时长
     * @param timeUnit 时间单位
     * @return 返回业务数据
     * @param <T> 结果泛型
     */
    <T> T queryWithLogicalExpireWithoutArgs(
            String keyPrefix,
            Class<T> clazz,
            Supplier<T> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 带参数查询集合数据，按照逻辑过期时间读取缓存数据，新开线程重建缓存，其他线程直接返回逻辑过期数据，不占用资源
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 缓存业务标识，也是查询数据库的参数
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 查询数据库的Function函数，接收id参数并返回查询结果
     * @param timeout 缓存逻辑过期时长
     * @param timeUnit 缓存逻辑过期时间单位
     * @return 业务数据
     * @param <T> 结果泛型
     * @param <ID> 查询数据库参数泛型，也是参数泛型类型
     */
    <T,ID> List<T> queryWithLogicalExpireList(
            String keyPrefix,
            ID id,
            Class<T> clazz,
            Function<ID,List<T>> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 不带参数查询集合数据，按照逻辑过期时间读取缓存数据，新开线程重建缓存，其他线程直接返回逻辑过期数据，不占用资源
     *
     * @param keyPrefix 缓存key的前缀
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 无参数查询数据库数据
     * @param timeout 缓存的时长
     * @param timeUnit 时间单位
     * @return 返回业务数据
     * @param <T> 结果泛型
     */
    <T> List<T> queryWithLogicalExpireListWithoutArgs(
            String keyPrefix,
            Class<T> clazz,
            Supplier<List<T>> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 带参数查询数据，按照互斥锁方式获取缓存数据，同一时刻只有一个线程访问数据库，其他线程访问不到数据重试
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 缓存业务标识，也是查询数据库的参数
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 查询数据库的Function函数，接收id参数并返回查询结果
     * @param timeout 缓存时长
     * @param timeUnit 时间单位
     * @return 查询到的对象，���果缓存不存在则通过dbFallback函数从数据库获取数据并缓存
     * @param <T> 缓存的值的泛型
     * @param <ID> 查询数据库参数的泛型，也是参数泛型类型
     */
    <T,ID> T queryWithMutex(
            String keyPrefix,
            ID id,
            Class<T> clazz,
            Function<ID,T> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 不带参数查询数据，按照互斥锁方式获取缓存数据，同一时刻只有一个线程访问数据库，其他线程访问不到数据重试
     *
     * @param keyPrefix 缓存key的前缀
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 无参数查询数据库数据
     * @param timeout 缓存时长
     * @param timeUnit 时间单位
     * @return 查询到的对象，如果缓存不存在则通过dbFallback函数从数据库获取数据并缓存
     * @param <T> 缓存的值的泛型
     */
    <T> T queryWithMutexWithoutArgs(
            String keyPrefix,
            Class<T> clazz,
            Supplier<T> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 带参数查询集合数据，按照互斥锁方式获取缓存数据，同一时刻只有一个线程访问数据库，其他线程访问不到数据重试
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 缓存业务标识，也是查询数据库的参数
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 查询数据库的Function函数，接收id参数并返回查询结果
     * @param timeout 缓存时长
     * @param timeUnit 时间单位
     * @return 查询到的对象，如果缓存不存在则通过dbFallback函数从数据库获取数据并缓存
     * @param <T> 缓存的值的泛型
     * @param <ID> 查询数据库参数的泛型，也是参数泛型类型
     */
    <T,ID> List<T> queryWithMutexList(
            String keyPrefix,
            ID id,
            Class<T> clazz,
            Function<ID,List<T>> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 不带参数查询集合数据，按照互斥锁方式获取缓存数据，同一时刻只有一个线程访问数据库，其他线程访问不到数据重试
     *
     * @param keyPrefix 缓存key的前缀
     * @param clazz 缓存的实际对象类型
     * @param dbFallback 无参数查询数据库数据
     * @param timeout 缓存时长
     * @param timeUnit 时间单位
     * @return 查询到的对象，如果缓存不存在则通过dbFallback函数从数据库获取数据并缓存
     * @param <T> 缓存的值的泛型
     */
    <T> List<T> queryWithMutexListWithoutArgs(
            String keyPrefix,
            Class<T> clazz,
            Supplier<List<T>> dbFallback,
            Long timeout,
            TimeUnit timeUnit
    );

    /**
     * 将对象转换为指定类型的结果
     *
     * @param obj 未知类型对象
     * @param clazz 转换后的目标类型
     * @return 转换后的泛型对象
     * @param <T> 泛型
     */
    default <T> T getResult(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        if(TypeConversion.isSimpleType(obj)) {
            return Convert.convert(clazz, obj);
        }
        return JSONUtil.toBean(JSONUtil.toJsonStr(obj), clazz);
    }

    /**
     * 将字符串转换为指定类型的结果的List
     *
     * @param obj 字符串对象
     * @param clazz 转换后的目标类型
     * @return  泛型List集合
     * @param <T> 泛型
     */
    default <T> List<T> getResultList(String obj, Class<T> clazz) {
        if(StrUtil.isEmpty(obj)) {
            return null;
        }
        return JSONUtil.toList(JSONUtil.parseArray(obj), clazz);
    }

    /**
     * 不确定参数类型的情况下，使用MD5计算参数的拼接到Redis中的唯一Key
     *
     * @param keyPrefix 缓存key的前缀
     * @param id 泛型参数
     * @return 拼接好的缓存key
     * @param <ID> 参数泛型类型
     */
    default <ID> String getKey(String keyPrefix, ID id) {
        if(id == null) {
            return keyPrefix;
        }

        String key = "";
        if(TypeConversion.isSimpleType(id)) {
            key = StrUtil.toString(id);
        }else{
            key = MD5.create().digestHex(JSONUtil.toJsonStr(id));
        }

        if(StrUtil.isEmpty(key)) {
            key = "";
        }
        return keyPrefix.concat(key);
    }

    /**
     * 获取简单的key
     *
     * @param key key
     * @return 返回key
     */
    default String getKey(String key){
        return getKey(key, null);
    }

    /**
     * 获取要保存到缓存中的value字符串，可能是简单类型，也可能是对象类型或集合数组等
     *
     * @param value 要保存的value值
     * @return 返回要保存到缓存中的value字符串
     */
    default String getValue(Object value){
        return TypeConversion.isSimpleType(value)?
                String.valueOf(value) :
                JSONUtil.toJsonStr(value);
    }

}
