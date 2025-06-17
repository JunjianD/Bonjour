package com.djj.bj.common.cache.id;

import com.djj.bj.common.cache.time.SystemTime;

import java.util.Date;

/**
 * 雪花算法
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.id
 * @className SnowFlake
 * @date 2025/6/11 22:42
 */
public class SnowFlake {
    /**
     * 起始的时间戳:2025-06-11 22:51:16
     */
    private static final long START_STMP = 1749653476681L; // 起始时间戳

    /**
     * 每一部分占用的位数
     */
    private static final long SEQUENCE_BIT = 12L; // 序列号占用的位数
    private static final long MACHINE_BIT = 5L; // 机器标识占用的位数
    private static final long DATACENTER_BIT = 5L; // 数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long dataCenterId;
    private long machineId;
    private long sequence = 0L; // 序列号
    private long lastStmp = -1L; // 上一次时间戳

    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATACENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATACENTER_NUM or less than 0, got: " + dataCenterId);
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0, got: " + machineId);
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    private long getNewTimeStamp() {
        // 获取当前时间戳
        return SystemTime.millisTime().now();
    }

    private long getNextMill() {
        long mill = getNewTimeStamp();
        while (mill <= lastStmp) {
            mill = getNewTimeStamp();
        }
        return mill;
    }

    public static long getMaxDataCenterNum() {
        return MAX_DATACENTER_NUM;
    }

    public static long getMaxMachineNum() {
        return MAX_MACHINE_NUM;
    }

    /**
     * 产生下一个ID
     */
    public synchronized long nextId() {
        long currStmp = getNewTimeStamp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastStmp - currStmp) + " milliseconds");
        } else if (currStmp == lastStmp) {
            // 如果当前时间戳和上次生成ID的时间戳相同，则序列号加1
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 如果序列号溢出，则等待下一毫秒
                // 此时sequence已经被重置为0
                currStmp = getNextMill();
            }
        } else {
            // 如果当前时间戳大于上次生成ID的时间戳，则序列号重置为0
            sequence = 0L;
        }
        lastStmp = currStmp;
        return ((currStmp - START_STMP) << TIMESTMP_LEFT)
                | (dataCenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }
}
