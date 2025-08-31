package com.djj.bj.platform.group.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.djj.bj.platform.common.model.entity.GroupMember;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.common.model.vo.GroupMemberVO;

import java.util.List;

/**
 * 群成员领域服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.service
 * @interfaceName GroupMenberDomainService
 * @date 2025/8/4 07:08
 */
public interface GroupMemberDomainService extends IService<GroupMember> {

    /**
     * 保存群成员
     *
     * @param groupMember 群成员实体
     * @return 是否保存成功
     */
    boolean saveGroupMember(GroupMember groupMember);

    /**
     * 批量保存群成员列表
     *
     * @param groupMemberList 群成员列表
     * @return 是否保存成功
     */
    boolean saveGroupMemberList(List<GroupMember> groupMemberList);

    /**
     * 更新群成员信息
     *
     * @param groupMember 群成员实体
     * @return 是否更新成功
     */
    boolean updateGroupMember(GroupMember groupMember);

    /**
     * 根据群组id移除所有成员
     *
     * @param groupId 群组id
     * @return 是否移除成功
     */
    boolean removeMemberByGroupId(Long groupId);

    /**
     * 根据用户id和群组id移除成员
     *
     * @param userId  用户id
     * @param groupId 群组id
     * @return 是否移除成功
     */
    boolean removeMember(Long userId, Long groupId);

    /**
     * 根据用户id和群组id获取群成员
     *
     * @param userId  用户id
     * @param groupId 群组id
     * @return 群成员实体
     */
    GroupMember getGroupMemberByUserIdAndGroupId(Long userId, Long groupId);

    /**
     * 根据群组id获取群成员列表
     *
     * @param groupId 群组id
     * @return 群成员列表
     */
    List<GroupMember> getGroupMemberListByGroupId(Long groupId);

    /**
     * 根据群组id获取群成员视图对象列表
     *
     * @param groupId 群组id
     * @return 群成员视图对象列表
     */
    List<GroupMemberVO> getGroupMemberVoListByGroupId(Long groupId);

    /**
     * 根据群组id获取用户id列表
     *
     * @param groupId 群组id
     * @return 用户id列表
     */
    List<Long> getUserIdsByGroupId(Long groupId);

    /**
     * 获取成员
     *
     * @param groupParams 群组参数
     * @return 群成员简单视图对象
     */
    GroupMemberSimpleVO getGroupMemberSimpleVO(GroupParams groupParams);

    /**
     * 根据用户id获取在各个群组中的信息
     *
     * @param userId 用户id
     * @return 用户在各个群组中的信息列表
     */
    List<GroupMemberSimpleVO> getGroupMemberSimpleVOList(Long userId);

    /**
     * 更新某个用户在所有群里的头像
     *
     * @param headImg 用户头像
     * @param userId  用户id
     * @return 是否更新成功
     */
    boolean updateHeadImgByUserId(String headImg, Long userId);

    /**
     * 保存群成员，使用事务模式
     *
     * @param groupMember 群成员实体
     */
    void saveInTransactionMode(GroupMember groupMember);
}
