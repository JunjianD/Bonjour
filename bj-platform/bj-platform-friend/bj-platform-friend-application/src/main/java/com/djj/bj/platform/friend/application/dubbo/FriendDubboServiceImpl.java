package com.djj.bj.platform.friend.application.dubbo;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.dubbo.friend.FriendDubboService;
import com.djj.bj.platform.friend.application.service.FriendService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 好友服务的Dubbo实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.application.dubbo
 * @className FriendDubboServiceImpl
 * @date 2025/8/1 21:43
 */
@Component
@DubboService(version = PlatformConstants.DEFAULT_DUBBO_VERSION)
public class FriendDubboServiceImpl implements FriendDubboService {
    @Resource
    private FriendService friendService;

    @Override
    public Boolean isFriend(Long userId1, Long userId2) {
        return friendService.isFriend(userId1, userId2);
    }

    @Override
    public List<Long> getFriendIdList(Long userId) {
        return friendService.getFriendIdList(userId);
    }

    @Override
    public List<Friend> getFriendByUserId(Long userId) {
        return friendService.getFriendByUserId(userId);
    }
}
