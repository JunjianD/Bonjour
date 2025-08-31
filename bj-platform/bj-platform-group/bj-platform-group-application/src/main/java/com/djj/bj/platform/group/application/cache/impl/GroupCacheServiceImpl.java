package com.djj.bj.platform.group.application.cache.impl;

import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.lock.DistributedLock;
import com.djj.bj.common.cache.lock.factory.DistributedLockFactory;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.common.model.vo.GroupMemberVO;
import com.djj.bj.platform.common.model.vo.GroupVO;
import com.djj.bj.platform.group.application.cache.GroupCacheService;
import com.djj.bj.platform.group.domain.event.GroupEvent;
import com.djj.bj.platform.group.domain.service.GroupDomainService;
import com.djj.bj.platform.group.domain.service.GroupMemberDomainService;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存更新
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.cache.impl
 * @className GroupCacheServiceImpl
 * @date 2025/8/5 00:45
 */
@Service
public class GroupCacheServiceImpl implements GroupCacheService {
    private final Logger logger = LoggerFactory.getLogger(GroupCacheServiceImpl.class);

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private GroupDomainService groupDomainService;

    @Resource
    private GroupMemberDomainService groupMemberDomainService;

    @Resource
    private DistributedLockFactory distributeLockFactory;

    @Resource
    private Client client;

    @Override
    public void updateGroupCache(GroupEvent groupEvent) {
        if (groupEvent == null || groupEvent.getEventId() == null) {
            logger.info("GroupCacheServiceImpl.updateGroupCache | 更新分布式缓存时，参数为空");
            return;
        }
        DistributedLock distributedLock = distributeLockFactory.getDistributedLock(PlatformConstants.getKey(
                PlatformConstants.GROUP_UPDATE_CACHE_LOCK_KEY,
                String.valueOf(groupEvent.getEventId())
        ));
        try {
            boolean success = distributedLock.tryLock();
            if (!success) {
                logger.info("GroupCacheServiceImpl.updateGroupCache | 更新分布式缓存时，获取锁失败，key:{}", PlatformConstants.GROUP_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(groupEvent.getEventId())));
                return;
            }
            switch (groupEvent.getHandler()) {
                case PlatformConstants.GROUP_HANDLER_INVITE -> this.handlerInvite(groupEvent);
                case PlatformConstants.GROUP_HANDLER_KICK -> this.handlerKick(groupEvent);
                case PlatformConstants.GROUP_HANDLER_QUIT -> this.handlerQuit(groupEvent);
                case PlatformConstants.GROUP_HANDLER_DELETE -> this.handlerDelete(groupEvent);
                case PlatformConstants.GROUP_HANDLER_MODIFY -> this.handlerModify(groupEvent);
                case PlatformConstants.GROUP_HANDLER_CREATE -> this.handlerCreate(groupEvent);
                default ->
                        logger.info("GroupCacheServiceImpl.updateGroupCache | 不存在此类事件 | 群组缓存服务接收到的事件参数为|{}", JSONObject.toJSONString(groupEvent));
            }
        } catch (Exception e) {
            logger.error("GroupCacheServiceImpl.updateGroupCache | 更新分布式缓存时发生异常 | {}", JSONObject.toJSONString(groupEvent), e);
        } finally {
            distributedLock.unlock();
        }
    }

    private void handlerInvite(GroupEvent groupEvent) {
        logger.info("GroupCacheServiceImpl | 进入邀人事件处理 | {}", JSONObject.toJSONString(groupEvent));
        Long groupId = groupEvent.getEventId();
        Long invitedId = groupEvent.getUserId();

        this.updateUserGroupList(invitedId);
        this.updateGroupVOInfo(invitedId, groupId, false, false);
        this.updateGroupMemberList(groupId, false);
        this.updateGroupMemberIdList(groupId, false);
        this.updateGroupMemberSimpleList(invitedId);
        this.updateGroupMemberSimple(invitedId, groupId, false, false);
    }

    private void handlerKick(GroupEvent groupEvent) {
        logger.info("GroupCacheServiceImpl | 进入踢人事件处理 | {}", JSONObject.toJSONString(groupEvent));
        Long groupId = groupEvent.getEventId();
        Long kickedId = groupEvent.getUserId();

        this.updateUserGroupList(kickedId);
        this.updateGroupVOInfo(kickedId, groupId, true, false);
        this.updateGroupMemberList(groupId, false);
        this.updateGroupMemberIdList(groupId, false);
        this.updateGroupMemberSimpleList(kickedId);
        this.updateGroupMemberSimple(kickedId, groupId, true, false);
    }

    private void handlerQuit(GroupEvent groupEvent) {
        logger.info("GroupCacheServiceImpl | 进入退群事件处理 | {}", JSONObject.toJSONString(groupEvent));

        Long groupId = groupEvent.getEventId();
        Long userId = groupEvent.getUserId();

        this.updateUserGroupList(userId);
        this.updateGroupVOInfo(userId, groupId, true, false);
        this.updateGroupMemberList(groupId, false);
        this.updateGroupMemberIdList(groupId, false);
        this.updateGroupMemberSimpleList(userId);
        this.updateGroupMemberSimple(userId, groupId, true, false);
    }

    private void handlerDelete(GroupEvent groupEvent) {
        logger.info("GroupCacheServiceImpl | 进入解散群事件处理 | {}", JSONObject.toJSONString(groupEvent));

        Long groupId = groupEvent.getEventId();
        Long userId = groupEvent.getUserId();
        List<Long> memberIds = groupEvent.getMemberIdList();

        // 个人缓存等自动更新
        this.updateGroupInfo(groupId, true);
        this.updateGroupMemberList(groupId, true);
        this.updateGroupMemberIdList(groupId, true);
        memberIds.forEach(memberId -> {
            this.deleteUserGroupList(memberId);
            this.updateGroupVOInfo(memberId, groupId, false, true);
            this.updateGroupMemberSimple(memberId, groupId, false, true);
        });
    }

    private void handlerModify(GroupEvent groupEvent) {
        logger.info("GroupCacheServiceImpl | 进入修改群事件处理 | {}", JSONObject.toJSONString(groupEvent));

        Long groupId = groupEvent.getEventId();
        Long userId = groupEvent.getUserId();

        Group group = this.updateGroupInfo(groupId, false);
        if (group.getOwnerId().equals(userId)) {
            // 群主修改群信息，立刻更新所有群成员的群信息缓存
            List<Long> memberIds = groupMemberDomainService.getUserIdsByGroupId(groupId);
            if (memberIds != null && !memberIds.isEmpty()) {
                String redisKey_eachGroupList;
                String redisKey_eachParam;
                for (Long memberId : memberIds) {
                    redisKey_eachGroupList = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_LIST_KEY, memberId);
                    distributeCacheService.delete(redisKey_eachGroupList);
                    redisKey_eachParam = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_VO_SINGLE_KEY, new GroupParams(memberId, groupId));
                    distributeCacheService.delete(redisKey_eachParam);
                }
            }
        }

        this.updateUserGroupList(userId);
        this.updateGroupVOInfo(userId, groupId, false, false);
        this.updateGroupMemberList(groupId, false);
        this.updateGroupMemberSimpleList(userId);
        this.updateGroupMemberSimple(userId, groupId, false, false);
    }

    private void handlerCreate(GroupEvent groupEvent) {
        logger.info("GroupCacheServiceImpl.handlerCreate | 进入保存群组事件处理|{}", JSONObject.toJSONString(groupEvent));

        Long groupId = groupEvent.getEventId();
        Long userId = groupEvent.getUserId();

        this.updateUserGroupList(userId);
        this.updateGroupVOInfo(userId, groupId, false, false);
        this.updateGroupMemberList(groupId, false);
        this.updateGroupMemberIdList(groupId, false);
        this.updateGroupMemberSimpleList(userId);
        this.updateGroupMemberSimple(userId, groupId, false, false);

    }

    /**
     * 更新个人的群组列表缓存
     *
     * @param userId 个人id
     */
    private void updateUserGroupList(Long userId) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_LIST_KEY, userId);
        List<GroupVO> groupVOList = groupDomainService.getGroupVOListByUserId(userId);

        if (groupVOList != null && !groupVOList.isEmpty()) {
            distributeCacheService.set(redisKey, groupVOList, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    /**
     * 删除个人的群组列表缓存
     *
     * @param userId 个人id
     */
    private void deleteUserGroupList(Long userId) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_LIST_KEY, userId);
        distributeCacheService.delete(redisKey);
    }

    /**
     * 更新群信息
     *
     * @param groupId 群组id
     * @param deleted 群组是否解散
     * @return 更新后的群组信息
     */
    private Group updateGroupInfo(Long groupId, boolean deleted) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_SINGLE_KEY, groupId);
        if (deleted) {
            distributeCacheService.delete(redisKey);
            return null;
        }
        Group group = groupDomainService.getGroupById(groupId);
        if (group != null) {
            distributeCacheService.set(redisKey, group, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        return group;
    }

    /**
     * 更新个人在单个群内的群组信息, 这一条包含了删除逻辑
     *
     * @param userId  用户id
     * @param groupId 群组id
     * @param quit    是否退出群组
     * @param deleted 是否解散群组
     */
    private void updateGroupVOInfo(Long userId, Long groupId, boolean quit, boolean deleted) {
        GroupParams params = new GroupParams(userId, groupId);
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_VO_SINGLE_KEY, params);

        if (quit || deleted) {
            distributeCacheService.delete(redisKey);
            return;
        }
        GroupVO groupVO = groupDomainService.getGroupVOByParams(params);
        if (groupVO != null) {
            distributeCacheService.set(redisKey, groupVO, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    /**
     * 更新群成员列表
     *
     * @param groupId 群组id
     * @param deleted 群组是否解散
     */
    private void updateGroupMemberList(Long groupId, boolean deleted) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_VO_LIST_KEY, groupId);
        if (deleted) {
            distributeCacheService.delete(redisKey);
            return;
        }
        List<GroupMemberVO> groupMemberVOS = groupMemberDomainService.getGroupMemberVoListByGroupId(groupId);
        List<Long> memberIds = groupMemberVOS.stream().map(GroupMemberVO::getUserId).toList();
        List<Long> onlineUserIds = client.getOnlineUserList(memberIds);

        List<GroupMemberVO> groupMemberVOList = groupMemberVOS.stream().peek(m -> m.setOnline(onlineUserIds.contains(m.getUserId())))
                .sorted((m1, m2) -> m2.getOnline().compareTo(m1.getOnline()))
                .toList();

        distributeCacheService.set(redisKey, groupMemberVOList, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 更新群成员ID列表
     *
     * @param groupId 群组id
     * @param deleted 群组是否解散
     */
    private void updateGroupMemberIdList(Long groupId, boolean deleted) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_ID_KEY, groupId);
        if (deleted) {
            distributeCacheService.delete(redisKey);
            return;
        }
        List<Long> memberIds = groupMemberDomainService.getUserIdsByGroupId(groupId);
        distributeCacheService.set(redisKey, memberIds, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 更新个人在每个群内的简易信息
     *
     * @param userId 用户id
     */
    private void updateGroupMemberSimpleList(Long userId) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_LIST_SIMPLE_KEY, userId);
        List<GroupMemberSimpleVO> groupMemberSimpleVOS = groupMemberDomainService.getGroupMemberSimpleVOList(userId);

        if (groupMemberSimpleVOS != null && !groupMemberSimpleVOS.isEmpty()) {
            distributeCacheService.set(redisKey, groupMemberSimpleVOS, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    /**
     * 删除个人在每个群内的简易信息缓存，这个一般不用, 等自动更新
     *
     * @param userId 用户id
     */
    private void deleteGroupMemberSimpleList(Long userId) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_LIST_SIMPLE_KEY, userId);
        distributeCacheService.delete(redisKey);
    }

    /**
     * 更新个人在单个群内的简易信息，这一条包含了删除逻辑
     *
     * @param userId  用户id
     * @param groupId 群组id
     */
    private void updateGroupMemberSimple(Long userId, Long groupId, boolean quit, boolean deleted) {
        GroupParams params = new GroupParams(userId, groupId);
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_VO_SIMPLE_KEY, params);
        if (quit || deleted) {
            distributeCacheService.delete(redisKey);
            return;
        }
        GroupMemberSimpleVO groupMemberSimpleVO = groupMemberDomainService.getGroupMemberSimpleVO(params);

        if (groupMemberSimpleVO != null) {
            distributeCacheService.set(redisKey, groupMemberSimpleVO, PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            distributeCacheService.set(redisKey, PlatformConstants.EMPTY_VALUE, PlatformConstants.DEFAULT_REDIS_CACHE_NULL_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

}
