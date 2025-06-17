package com.djj.bj.application.netty.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户连接缓存类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.cache
 * @className UserChannelCtxCache
 * @date 2025/6/15 21:22
 */
public class UserChannelCtxCache {
    private static Map<Long, Map<Integer, ChannelHandlerContext>> ctxMap = new ConcurrentHashMap<>();

    public static void addCtx(Long userId, Integer terminal, ChannelHandlerContext ctx) {
        ctxMap.computeIfAbsent(userId, key -> new ConcurrentHashMap<>()).put(terminal, ctx);
    }

    public static void removeCtx(Long userId, Integer terminal) {
        if (userId != null && terminal != null && ctxMap.containsKey(userId)) {
            ctxMap.get(userId).remove(terminal);
        }
    }

    public static ChannelHandlerContext getCtx(Long userId, Integer terminal) {
        if (userId != null && terminal != null && ctxMap.containsKey(userId)) {
            return ctxMap.get(userId).get(terminal);
        }
        return null;
    }

    public static Map<Integer, ChannelHandlerContext> getCtxs(Long userId) {
        if (userId == null) {
            return null;
        }
        return ctxMap.get(userId);
    }
}
