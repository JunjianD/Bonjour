package com.djj.bj.platform.group.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.entity.GroupMember;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.common.model.vo.GroupMemberVO;
import com.djj.bj.platform.group.domain.repository.GroupMemberRepository;
import com.djj.bj.platform.group.domain.service.GroupMemberDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 群成员领域服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.service.impl
 * @className GroupMemberDomainService
 * @date 2025/8/4 07:47
 */
@Service
public class GroupMemberDomainServiceImpl extends ServiceImpl<GroupMemberRepository, GroupMember> implements GroupMemberDomainService {
    private final Logger logger = LoggerFactory.getLogger(GroupMemberDomainServiceImpl.class);

    @Override
    public boolean saveGroupMember(GroupMember groupMember) {
        if (groupMember == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.insert(groupMember) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveGroupMemberList(List<GroupMember> groupMemberList) {
        if (CollectionUtil.isEmpty(groupMemberList)) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return this.saveOrUpdateBatch(groupMemberList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateGroupMember(GroupMember groupMember) {
        if (groupMember == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return this.updateById(groupMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMemberByGroupId(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<GroupMember> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(GroupMember::getGroupId, groupId)
                .set(GroupMember::getQuit, true);
        return this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMember(Long userId, Long groupId) {
        if (userId == null || groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<GroupMember> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId)
                .set(GroupMember::getQuit, true);
        return this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupMember getGroupMemberByUserIdAndGroupId(Long userId, Long groupId) {
        if (userId == null || groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<GroupMember> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId)
                .eq(GroupMember::getQuit, false);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<GroupMember> getGroupMemberListByGroupId(Long groupId) {
        LambdaQueryWrapper<GroupMember> memberWrapper = Wrappers.lambdaQuery();
        memberWrapper.eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getQuit, false);
        return this.list(memberWrapper);
    }

    @Override
    public List<GroupMemberVO> getGroupMemberVoListByGroupId(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getGroupMemberVoListByGroupId(groupId);
    }

    @Override
    public List<Long> getUserIdsByGroupId(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return baseMapper.getUserIdsByGroupId(groupId);
    }

    @Override
    public GroupMemberSimpleVO getGroupMemberSimpleVO(GroupParams groupParams) {
        return baseMapper.getGroupMemberSimpleVO(groupParams.getGroupId(), groupParams.getUserId());
    }

    @Override
    public List<GroupMemberSimpleVO> getGroupMemberSimpleVOList(Long userId) {
        return baseMapper.getGroupMemberSimpleVOList(userId);
    }

    @Override
    public boolean updateHeadImgByUserId(String headImg, Long userId) {
        return baseMapper.updateHeadImgByUserId(headImg, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveInTransactionMode(GroupMember groupMember) {
        this.save(groupMember);
    }
}
