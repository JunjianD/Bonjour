package com.djj.bj.platform.common.threadpool;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 私聊消息线程池工具类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.threadpool
 * @className PrivateMessageThreadPoolUtils
 * @date 2025/8/9 16:09
 */
public class PrivateMessageThreadPoolUtils {
    private static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            64,
            256,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(1);

                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r, "PrivateMsg-Thread-" + count.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void execute(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return THREAD_POOL_EXECUTOR.submit(callable);
    }

    public static void shutdown() {
        THREAD_POOL_EXECUTOR.shutdown();
    }
}
