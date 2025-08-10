package com.djj.bj.platform.dubbo.group;

import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;

import java.util.List;

/**
 * 群组Dubbo服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.dubbo.group
 * @interfaceName GroupDubboService
 * @date 2025/8/4 21:38
 */
public interface GroupDubboService {

    /**
     * 检测群组是否存在
     *
     * @param groupId 群组id
     * @return true/false
     */
    boolean isExists(Long groupId);

    /**
     * 获取成员
     *
     * @param groupParams 群组参数
     * @return
     */
    GroupMemberSimpleVO getGroupMemberSimpleVO(GroupParams groupParams);

    /**
     * 根据群组id获取群成员列表
     *
     * @param groupId 群组id
     * @return 群成员列表
     */
    List<Long> getUserIdsByGroupId(Long groupId);

    /**
     * 根据用户id获取在各个群组中的信息
     *
     * @param userId 用户id
     * @return 用户在各个群组中的信息列表
     */
    List<GroupMemberSimpleVO> getGroupMemberSimpleVOList(Long userId);

    /**
     * 根据用户id获取群组id列表
     */
    List<Long> getGroupIdsByUserId(Long userId);
}
