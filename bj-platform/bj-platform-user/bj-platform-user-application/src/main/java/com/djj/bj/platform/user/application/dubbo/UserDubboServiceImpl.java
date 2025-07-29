package com.djj.bj.platform.user.application.dubbo;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.dubbo.user.UserDubboService;
import com.djj.bj.platform.user.application.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * 用户的Dubbo服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.dubbo
 * @className UserDubboServiceImpl
 * @date 2025/7/26 19:17
 */
@Component
@DubboService(version = PlatformConstants.DEFAULT_DUBBO_VERSION)
public class UserDubboServiceImpl implements UserDubboService {
    @Resource
    private UserService userService;

    @Override
    public User getUserById(Long userId) {
        return userService.getUserById(userId);
    }
}
