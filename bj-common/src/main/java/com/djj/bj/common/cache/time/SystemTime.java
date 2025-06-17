package com.djj.bj.common.cache.time;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 高并发获取时间戳性能优化
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.time
 * @className SystemTime
 * @date 2025/6/11 21:07
 */
public class SystemTime {
    private final Long precision;
    private final AtomicLong now;
    private static final String THREAD_NAME = "system.clock";

    private void scheduleTimeUpdating() {
        // 添加定时任务来更新now的值
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutorService.scheduleAtFixedRate(
                () -> now.set(System.currentTimeMillis()),
                precision,
                precision,
                TimeUnit.MILLISECONDS
        );
    }

    private SystemTime(Long precision) {
        this.precision = precision;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleTimeUpdating();
    }

    private static final SystemTime MILLIS_TIME = new SystemTime(1L);

    public static SystemTime millisTime() {
        return MILLIS_TIME;
    }

    public Long now() {
        return now.get();
    }

}
