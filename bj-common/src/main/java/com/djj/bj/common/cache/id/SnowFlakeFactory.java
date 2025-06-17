package com.djj.bj.common.cache.id;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 雪花算法工厂类，用于创建和管理SnowFlake实例。
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.id
 * @className SnowFlakeFactory
 * @date 2025/6/12 21:58
 */
public class SnowFlakeFactory {
    /**
     * 默认数据中心ID
     */
    private static final long DEFAULT_DATACENTER_ID = 1;

    /**
     * 默认机器ID
     */
    private static final long DEFAULT_MACHINE_ID = 1;

    /**
     * 默认雪花算法句柄名称
     */
    private static final String DEFAULT_SNOW_FLAKE = "snow_flake";

    /**
     * 缓存SnowFlake实例
     */
    private static ConcurrentMap<String,SnowFlake> snowFlakesCache = new ConcurrentHashMap<>(4);

    /**
     * 获取一个新的SnowFlake实例
     *
     * @param datacenterId 数据中心ID
     * @param machineId    机器ID
     * @return SnowFlake实例
     */
    public static SnowFlake getSnowFlake(long datacenterId, long machineId) {
        return new SnowFlake(datacenterId, machineId);
    }

    /**
     * 获取默认的SnowFlake实例
     *
     * @return 默认的SnowFlake实例
     */
    public static SnowFlake getSnowFlake() {
        return new SnowFlake(DEFAULT_DATACENTER_ID, DEFAULT_MACHINE_ID);
    }

    /**
     * 从缓存中获取默认的SnowFlake实例
     *
     * @return 缓存中的默认SnowFlake实例
     */
    public static SnowFlake getSnowFlakeFromCache() {
        SnowFlake snowFlake = snowFlakesCache.get(DEFAULT_SNOW_FLAKE);
        if (snowFlake == null) {
            snowFlake = new SnowFlake(DEFAULT_DATACENTER_ID, DEFAULT_MACHINE_ID);
            snowFlakesCache.put(DEFAULT_SNOW_FLAKE, snowFlake);
        }
        return snowFlake;
    }

    /**
     * 根据数据中心ID和机器ID从缓存中获取SnowFlake实例
     *
     * @param dataCenterId 数据中心ID
     * @param machineId    机器ID
     * @return SnowFlake实例
     */
    public static SnowFlake getSnowFlakeByDataCenterIdAndMachineIdFromCache(Long dataCenterId, Long machineId) {
        if (dataCenterId > SnowFlake.getMaxDataCenterNum() || dataCenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0, got: " + dataCenterId);
        }
        if (machineId > SnowFlake.getMaxMachineNum() || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0, got: " + machineId);
        }
        String key = DEFAULT_SNOW_FLAKE.concat("_").concat(String.valueOf(dataCenterId)).concat("_").concat(String.valueOf(machineId));
        SnowFlake snowFlake = snowFlakesCache.get(key);
        if (snowFlake == null) {
            snowFlake = new SnowFlake(dataCenterId, machineId);
            snowFlakesCache.put(key, snowFlake);
        }
        return snowFlake;
    }
}
