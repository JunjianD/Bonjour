package com.djj.bj.platform.group.application.dubbo;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.dubbo.group.GroupDubboService;
import com.djj.bj.platform.group.application.service.GroupService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 群组Dubbo服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.dubbo
 * @className GroupDubboServiceImpl
 * @date 2025/8/4 21:58
 */
@Component
@DubboService(version = PlatformConstants.DEFAULT_DUBBO_VERSION)
public class GroupDubboServiceImpl implements GroupDubboService {
    @Resource
    private GroupService groupService;

    @Override
    public boolean isExists(Long groupId) {
        Group group = groupService.getById(groupId);
        if (Objects.isNull(group)) {
            return false;
        }
        if (group.getDeleted()) {
            return false;
        }
        return true;
    }

    @Override
    public GroupMemberSimpleVO getGroupMemberSimpleVO(GroupParams groupParams) {
        return groupService.getGroupMemberSimpleVO(groupParams);
    }

    @Override
    public List<Long> getUserIdsByGroupId(Long groupId) {
        return groupService.getUserIdsByGroupId(groupId);
    }

    @Override
    public List<GroupMemberSimpleVO> getGroupMemberSimpleVOList(Long userId) {
        return groupService.getGroupMemberSimpleVOList(userId);
    }
}
