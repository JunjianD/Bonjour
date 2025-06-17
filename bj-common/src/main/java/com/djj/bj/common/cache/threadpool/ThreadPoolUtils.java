package com.djj.bj.common.cache.threadpool;

import java.util.concurrent.*;

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
            16,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4096),
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
