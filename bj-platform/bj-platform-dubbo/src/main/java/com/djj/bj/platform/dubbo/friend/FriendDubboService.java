package com.djj.bj.platform.dubbo.friend;

import com.djj.bj.platform.common.model.entity.Friend;

import java.util.List;

/**
 * 好友服务的Dubbo接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.dubbo.friend
 * @interfaceName FriendDubboService
 * @date 2025/8/1 21:30
 */
public interface FriendDubboService {
    /**
     * 判断用户2是否是用户1的好友
     *
     * @param userId1 用户1的id
     * @param userId2 用户2的id
     * @return 如果用户2是用户1的好友，返回true；否则返回false
     */
    Boolean isFriend(Long userId1, Long userId2);

    /**
     * 获取用户的好友列表
     *
     * @param userId 用户id
     * @return 好友id列表
     */
    List<Long> getFriendIdList(Long userId);

    /**
     * 根据用户id获取好友信息列表
     *
     * @param userId 用户id
     * @return 好友信息列表
     */
    List<Friend> getFriendByUserId(Long userId);
}
