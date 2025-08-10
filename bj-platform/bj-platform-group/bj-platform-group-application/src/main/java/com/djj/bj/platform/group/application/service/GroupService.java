package com.djj.bj.platform.group.application.service;

import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupInviteVO;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.common.model.vo.GroupMemberVO;
import com.djj.bj.platform.common.model.vo.GroupVO;

import java.util.List;

/**
 * 群组服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.service
 * @interfaceName GroupService
 * @date 2025/8/4 08:38
 */
public interface GroupService {
    /**
     * 创建群组
     *
     * @param vo 群组信息
     * @return 创建后的群组信息
     */
    GroupVO createGroup(GroupVO vo);

    /**
     * 修改群组信息
     *
     * @param vo 群组信息
     * @return 修改后的群组信息
     */
    GroupVO modifyGroup(GroupVO vo);

    /**
     * 删除群组
     *
     * @param groupId 群组ID
     */
    void deleteGroup(Long groupId);

    /**
     * 退出群组
     *
     * @param groupId 群组ID
     */
    void quitGroup(Long groupId);

    /**
     * 踢出群组成员
     *
     * @param groupId 群组ID
     * @param userId  被踢出用户ID
     */
    void kickGroup(Long groupId, Long userId);

    /**
     * 获取用户的群组列表
     *
     * @return 用户的群组列表
     */
    List<GroupVO> findGroups();

    /**
     * 邀请用户加入群组
     *
     * @param vo 邀请信息
     */
    void invite(GroupInviteVO vo);

    /**
     * 根据群组ID获取群组信息,并进行缓存
     *
     * @param groupId 群组ID
     * @return 群组信息
     */
    Group getById(Long groupId);

    /**
     * 根据群组ID获取群组视图对象，查找群聊
     *
     * @param groupId 群组ID
     * @return 群组视图对象
     */
    GroupVO findById(Long groupId);

    /**
     * 根据群聊id查询群成员
     *
     * @param groupId 群组ID
     * @return 群成员视图对象列表
     */
    List<GroupMemberVO> findGroupMembers(Long groupId);

    /**
     * 获取群成员群备注昵称和是否退出的状态
     *
     * @param groupParams 群参数
     * @return 群成员简单视图对象
     */
    GroupMemberSimpleVO getGroupMemberSimpleVO(GroupParams groupParams);

    /**
     * 获取群成员id列表
     *
     * @param groupId 群组ID
     * @return 群成员ID列表
     */
    List<Long> getUserIdsByGroupId(Long groupId);

    /**
     * 根据用户id获取在各个群组中的信息
     *
     * @param userId 用户ID
     * @return 用户在各个群组中的信息
     */
    List<GroupMemberSimpleVO> getGroupMemberSimpleVOList(Long userId);

    /**
     * 更新某个用户在所有群的头像
     *
     * @param headImg 用户头像
     * @param userId  用户ID
     * @return 是否更新成功
     */
    boolean updateHeadImgByUserId(String headImg, Long userId);

    /**
     * 根据用户id拉取群组id列表
     */
    List<Long> getGroupIdsByUserId(Long userId);
}
