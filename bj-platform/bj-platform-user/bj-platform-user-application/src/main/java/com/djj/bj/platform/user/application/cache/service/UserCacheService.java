package com.djj.bj.platform.user.application.cache.service;

/**
 * 用户缓存接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.cache.service
 * @interfaceName UserCacheService
 * @date 2025/7/25 18:12
 */
public interface UserCacheService {

    /**
     * 更新用户缓存
     *
     * @param userId 用户ID
     */
    void updateUserCache(Long userId);
}
