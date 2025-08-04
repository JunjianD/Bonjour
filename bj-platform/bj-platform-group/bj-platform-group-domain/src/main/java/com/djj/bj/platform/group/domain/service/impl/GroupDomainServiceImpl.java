package com.djj.bj.platform.group.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupVO;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.group.domain.repository.GroupMemberRepository;
import com.djj.bj.platform.group.domain.repository.GroupRepository;
import com.djj.bj.platform.group.domain.service.GroupDomainService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 群组领域服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.service.impl
 * @className GroupDomainServiceImpl
 * @date 2025/8/4 07:12
 */
@Service
public class GroupDomainServiceImpl extends ServiceImpl<GroupRepository, Group> implements GroupDomainService {
    private final Logger logger = LoggerFactory.getLogger(GroupDomainServiceImpl.class);

    @Resource
    private GroupMemberRepository groupMemberRepository;

    @Override
    public GroupVO createGroup(GroupVO vo, Long userId) {
        if (vo == null || userId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR);
        }
        // 保存群组信息
        vo.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        Group group = BeanUtils.copyProperties(vo, Group.class);
        // 设置群主id
        group.setOwnerId(userId);
        group.setCreatedTime(new Date());
        int count = baseMapper.insert(group);
        if (count <= 0) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "创建群组失败");
        }
        vo.setRemark(StrUtil.isEmpty(group.getName()) ? vo.getRemark() : group.getName());
        return vo;
    }

    @Override
    public GroupVO modifyGroup(GroupVO vo, Long userId) {
        if (vo == null || userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        Group group = this.getGroupById(vo.getId());
        if (group == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群不存在");
        }
        //只有群主才懂更新群信息
        if (group.getOwnerId().equals(userId)) {
            group = BeanUtils.copyProperties(vo, Group.class);
            this.updateById(group);
        }
        vo.setRemark(StrUtil.isEmpty(vo.getRemark()) ? group.getName() : vo.getRemark());
        return vo;
    }

    @Override
    public boolean deleteGroup(Long groupId, Long userId) {
        if (groupId == null || userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        Group group = this.getGroupById(groupId);
        if (group == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群不存在");
        }
        if (!group.getOwnerId().equals(userId)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "只有群主才有权限解散群聊");
        }
        group.setDeleted(true);
        return this.updateById(group);
    }

    @Override
    public boolean quitGroup(Long groupId, Long userId) {
        if (groupId == null || userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        Long ownerId = baseMapper.getOwnerId(groupId);
        if (ownerId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群不存在");
        }
        if (ownerId.equals(userId)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "您是群主，不可退出群聊");
        }
        return true;
    }

    @Override
    public boolean kickGroup(Long groupId, Long kickUserId, Long userId) {
        if (groupId == null || kickUserId == null || userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        Long ownerId = baseMapper.getOwnerId(groupId);
        if (ownerId == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群不存在");
        }
        if (!ownerId.equals(userId)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "您不是群主，没有权限踢人");
        }
        if (kickUserId.equals(userId)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "不能自己踢自己");
        }
        return true;
    }

    @Override
    public GroupVO getGroupVOById(Long groupId, Long userId) {
        if (groupId == null || userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getGroupVOById(groupId, userId);
    }

    @Override
    public GroupVO getGroupVOByParams(GroupParams groupParams) {
        if (groupParams == null || groupParams.isEmpty()) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getGroupVOById(groupParams.getGroupId(), groupParams.getUserId());
    }

    @Override
    public Group getGroupById(Long groupId) {
        Group group = this.getById(groupId);
        if (group == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群组不存在");
        }
        if (group.getDeleted()) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群组'" + group.getName() + "'已解散");
        }
        return group;
    }

    @Override
    public List<GroupVO> getGroupVOListByUserId(Long userId) {
        if (userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getGroupVOListByUserId(userId);
    }

    @Override
    public String getGroupName(Long groupId) {
        return baseMapper.getGroupName(groupId);
    }
}
