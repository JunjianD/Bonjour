package com.djj.bj.platform.group.application.cache;

import com.djj.bj.platform.group.domain.event.GroupEvent;

/**
 * 群组缓存服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.cache
 * @interfaceName GroupCacheService
 * @date 2025/8/5 00:44
 */
public interface GroupCacheService {
    /**
     * 更新群组缓存
     *
     * @param groupEvent 群组事件对象
     */
    void updateGroupCache(GroupEvent groupEvent);
}
