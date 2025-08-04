package com.djj.bj.platform.friend.application.cache;

import com.djj.bj.platform.friend.domain.event.FriendEvent;

/**
 * 好友缓存服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.cache
 * @interfaceName FriendCacheService
 * @date 2025/8/2 15:45
 */
public interface FriendCacheService {
    /**
     * 更新好友缓存
     *
     * @param friendEvent 好友事件
     */
    void updateFriendCache(FriendEvent friendEvent);
}
