package com.djj.bj.platform.common.model.constants;

/**
 * 大后端平台常量类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.constants
 * @className PlatformConstants
 * @date 2025/7/14 16:28
 */
public class PlatformConstants {
    /**
     * 缓存数据默认过期时间，单位为分钟
     */
    public static final Long DEFAULT_REDIS_CACHE_EXPIRE_TIME = 10L;

    /**
     * 大后端平台的用户key
     */
    public static final String PLATFORM_REDIS_USER_KEY = "platform:user:";

    /**
     * Session数据
     */
    public static final String SESSION = "session";

    /**
     * 风控前缀
     */
    public static final String RISK_CONTROL_KEY_PREFIX = "risk:control:";

    /**
     * AccessToken
     */
    public static final String ACCESS_TOKEN = "accessToken";

    /**
     * 本地事件类型
     */
    public static final String EVENT_PUBLISH_TYPE_LOCAL = "local";

    /**
     * RocketMQ事件类型
     */
    public static final String EVENT_PUBLISH_TYPE_ROCKETMQ = "rocketmq";

    /**
     * 用户事件TOPIC
     */
    public static final String TOPIC_EVENT_ROCKETMQ_USER = "topic_event_rocketmq_user";

    /**
     * 本地订阅事件
     */
    public static final String TOPIC_EVENT_LOCAL = "topic_event_local";

    /**
     * 更新用户分布式缓存时用的锁前缀
     */
    public static final String IM_USER_UPDATE_CACHE_LOCK_KEY = "IM_USER_UPDATE_CACHE_LOCK_KEY_";

    /**
     * 用户事件消费分组
     */
    public static final String EVENT_USER_CONSUMER_GROUP = "event_user_consumer_group";

    public static String getKey(String prefix, String key) {
        return prefix.concat(key);
    }
}
