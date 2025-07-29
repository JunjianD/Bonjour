package com.djj.bj.platform.dubbo.user;

import com.djj.bj.platform.common.model.entity.User;

/**
 * 用户的Dubbo服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.dubbo.user
 * @interfaceName UserDubboService
 * @date 2025/7/26 16:40
 */
public interface UserDubboService {

    /**
     * 根据用户id获取用户实体对象
     *
     * @param userId 用户id
     * @return 用户对象
     */
    User getUserById(Long userId);
}
