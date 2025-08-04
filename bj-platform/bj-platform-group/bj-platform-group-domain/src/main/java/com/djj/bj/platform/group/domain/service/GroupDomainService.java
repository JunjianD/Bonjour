package com.djj.bj.platform.group.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupVO;

import java.util.List;

/**
 * 群组领域服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.service
 * @interfaceName GroupDomainService
 * @date 2025/8/4 05:26
 */
public interface GroupDomainService extends IService<Group> {
    /**
     * 创建群组
     *
     * @param vo     群组信息
     * @param userId 创建者用户ID
     * @return 创建后的群组信息
     */
    GroupVO createGroup(GroupVO vo, Long userId);

    /**
     * 更新群组信息
     *
     * @param vo     群组信息
     * @param userId 更新者用户ID
     * @return 更新后的群组信息
     */
    GroupVO modifyGroup(GroupVO vo, Long userId);

    /**
     * 删除群组
     *
     * @param groupId 群组ID
     * @param userId  删除者用户ID
     * @return 是否删除成功
     */
    boolean deleteGroup(Long groupId, Long userId);

    /**
     * 退群
     *
     * @param groupId 群组ID
     * @param userId  退群者用户ID
     * @return 是否退群成功
     */
    boolean quitGroup(Long groupId, Long userId);

    /**
     * 踢人出群
     *
     * @param groupId    群组ID
     * @param kickUserId 被踢用户ID
     * @param userId     操作用户ID
     * @return 是否踢人成功
     */
    boolean kickGroup(Long groupId, Long kickUserId, Long userId);

    /**
     * 根据id获取群组信息
     *
     * @param groupId 群组ID
     * @param userId  请求用户ID
     * @return 群组信息
     */
    GroupVO getGroupVOById(Long groupId, Long userId);

    /**
     * 根据参数获取群组信息
     *
     * @param groupParams 群组命令
     * @return 群组信息
     */
    GroupVO getGroupVOByParams(GroupParams groupParams);

    /**
     * 根据id获取群组信息
     *
     * @param groupId 群组ID
     * @return 群组实体
     */
    Group getGroupById(Long groupId);

    /**
     * 获取用户所在的群组列表
     *
     * @param userId 用户ID
     * @return 群组信息列表
     */
    List<GroupVO> getGroupVOListByUserId(Long userId);

    /**
     * 获取群组名称
     *
     * @param groupId 群组ID
     * @return 群组名称
     */
    String getGroupName(Long groupId);
}
