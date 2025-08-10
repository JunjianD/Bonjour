package com.djj.bj.platform.common.threadpool;

import java.util.concurrent.*;

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
            8,
            16,
            120,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4096),
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
