package com.djj.bj.platform.common.risk.window.impl;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.risk.window.SlidingWindowLimitService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis实现的滑动窗口限流服务
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.window.impl
 * @className RedisSlidingWindowLimitService
 * @date 2025/7/19 16:25
 */
@Component
@ConditionalOnProperty(name = "distributed.cache.type", havingValue = "redis")
public class RedisSlidingWindowLimitService implements SlidingWindowLimitService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean passThough(String key, long windowPeriod, int windowSize) {
        // 风控key
        String riskControlKey = PlatformConstants.getKey(PlatformConstants.RISK_CONTROL_KEY_PREFIX, key);

        // 获取当前时间
        long currentTimeStamp = System.currentTimeMillis();
        long length = windowPeriod * windowSize;
        long start = currentTimeStamp - length;
        // 计算过期时间
        long expireTime = length + windowPeriod;
        // 添加当前时间戳到有序集合
        redisTemplate.opsForZSet().add(riskControlKey, String.valueOf(currentTimeStamp), currentTimeStamp);
        // 移除[0,start]区间内的值
        redisTemplate.opsForZSet().removeRangeByScore(riskControlKey, 0, start);
        // 获取窗口内元素个数
        Long count = redisTemplate.opsForZSet().zCard(riskControlKey);
        // 设置过期时间
        redisTemplate.expire(riskControlKey, expireTime, TimeUnit.MILLISECONDS);
        // 如果count为空，不能通过
        if (count == null) {
            return false;
        }
        // 返回是否通过限流
        return count <= windowSize;
    }
}
