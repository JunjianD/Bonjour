package com.djj.bj.common.cache.threadpool;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工具类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.threadpool
 * @className ThreadPoolUtils
 * @date 2025/6/4 21:11
 */
public class ThreadPoolUtils {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            16,
            32,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4096),
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(1);
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r, "CacheTask-Thread-" + count.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }
    public static <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
