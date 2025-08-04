package com.djj.bj.platform.friend.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.vo.FriendVO;
import com.djj.bj.platform.friend.domain.model.command.FriendCommand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 好友领域服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.domain.service
 * @interfaceName FriendDomainService
 * @date 2025/8/1 15:14
 */
public interface FriendDomainService extends IService<Friend> {

    /**
     * 根据用户id获取好友的id列表
     *
     * @param userId 用户ID
     * @return 好友ID列表
     */
    List<Long> getFriendIdList(Long userId);

    /**
     * 根据用户id获取好友列表
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    List<FriendVO> findFriendByUserId(Long userId);

    /**
     * 根据用户ID获取好友信息
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    List<Friend> getFriendByUserId(Long userId);

    /**
     * 判断两个用户是否是好友关系
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @return 是否是好友关系
     */
    Boolean isFriend(Long userId1, Long userId2);

    /**
     * 绑定好友关系
     *
     * @param friendCommand 好友命令对象
     * @param headImg       好友头像
     * @param nickName      好友昵称
     */
    void bindFriend(FriendCommand friendCommand, String headImg, String nickName);

    /**
     * 解除好友关系
     *
     * @param friendCommand 好友命令对象
     */
    void unbindFriend(FriendCommand friendCommand);

    /**
     * 更新好友数据
     *
     * @param vo     好友视图对象
     * @param userId 用户ID
     */
    void update(FriendVO vo, Long userId);

    /**
     * 获取好友信息
     *
     * @param friendCommand 好友命令对象
     * @return 好友视图对象
     */
    FriendVO findFriend(FriendCommand friendCommand);

    /**
     * 更新好友信息
     *
     * @param headImage 好友头像
     * @param nickName  好友昵称
     * @param friendId  好友ID
     * @return 更新结果
     */
    boolean updateFriendByFriendId(@Param("headImage") String headImage, String nickName, Long friendId);

}
