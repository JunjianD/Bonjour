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
}
