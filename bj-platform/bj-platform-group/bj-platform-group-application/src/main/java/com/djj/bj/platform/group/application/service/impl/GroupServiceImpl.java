package com.djj.bj.platform.group.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.mq.event.MessageEventSenderService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.entity.GroupMember;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.common.model.vo.GroupInviteVO;
import com.djj.bj.platform.common.model.vo.GroupMemberSimpleVO;
import com.djj.bj.platform.common.model.vo.GroupMemberVO;
import com.djj.bj.platform.common.model.vo.GroupVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.dubbo.friend.FriendDubboService;
import com.djj.bj.platform.dubbo.user.UserDubboService;
import com.djj.bj.platform.group.application.service.GroupService;
import com.djj.bj.platform.group.domain.event.GroupEvent;
import com.djj.bj.platform.group.domain.service.GroupDomainService;
import com.djj.bj.platform.group.domain.service.GroupMemberDomainService;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 群组服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.application.service.impl
 * @className GroupServiceImpl
 * @date 2025/8/4 08:41
 */
@Service
//@CacheConfig(cacheNames = Constants.CACHE_GROUP_INFO)
public class GroupServiceImpl implements GroupService {
    private final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Resource
    private Client client;

    @Resource
    private GroupDomainService groupDomainService;

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private GroupMemberDomainService groupMemberDomainService;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false)
    private UserDubboService userDubboService;

    @DubboReference(version = PlatformConstants.DEFAULT_DUBBO_VERSION, check = false)
    private FriendDubboService friendDubboService;

    @Value("${message.mq.event.type}")
    private String eventType;

    @Resource
    private MessageEventSenderService messageEventSenderService;

    @Override
    public GroupVO createGroup(GroupVO vo) {
        if (vo == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        User user = userDubboService.getUserById(session.getUserId());
        if (user == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "未获取到用户信息");
        }
        vo = this.getGroupVO(groupDomainService.createGroup(vo, session.getUserId()), session, user);
        logger.info("GroupServiceImpl.createGroup | 创建群聊 | 群聊ID: {}, 群聊名称: {}", vo.getId(), vo.getName());
        GroupEvent groupEvent = new GroupEvent(vo.getId(), user.getUserId(), PlatformConstants.GROUP_HANDLER_CREATE, this.getTopicEvent(), Collections.emptyList());
        messageEventSenderService.send(groupEvent);
        return vo;
    }

    private GroupVO getGroupVO(GroupVO vo, UserSession session, User user) {
        // 把群主加入群
        GroupMember groupMember = new GroupMember();
        groupMember.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        groupMember.setGroupId(vo.getId());
        groupMember.setUserId(user.getUserId());
        groupMember.setHeadImage(user.getHeadImageThumb());
        groupMember.setAliasName(StringUtils.isEmpty(vo.getAliasName()) ? session.getNickName() : vo.getAliasName());
        groupMember.setRemark(vo.getRemark());
        groupMember.setCreatedTime(new Date());
        groupMemberDomainService.saveInTransactionMode(groupMember);
        vo.setAliasName(groupMember.getAliasName());
        vo.setRemark(groupMember.getRemark());
        return vo;
    }

    @Override
    public GroupVO modifyGroup(GroupVO vo) {
        if (vo == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        //更新群组信息
        vo = groupDomainService.modifyGroup(vo, session.getUserId());
        GroupMember groupMember = groupMemberDomainService.getGroupMemberByUserIdAndGroupId(session.getUserId(), vo.getId());
        if (groupMember == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "您不是群聊的成员");
        }
        groupMember.setAliasName(StringUtils.isEmpty(vo.getAliasName()) ? session.getNickName() : vo.getAliasName());
        groupMember.setRemark(vo.getRemark());
        if (groupMemberDomainService.updateGroupMember(groupMember)) {
            logger.info("GroupServiceImpl.modifyGroup | 修改群聊 | 群聊ID: {}, 群聊名称: {}", vo.getId(), vo.getName());
            GroupEvent groupEvent = new GroupEvent(vo.getId(), session.getUserId(), PlatformConstants.GROUP_HANDLER_MODIFY, this.getTopicEvent(), Collections.emptyList());
            messageEventSenderService.send(groupEvent);
        }
        return vo;
    }

    @Override
    public void deleteGroup(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        List<Long> userIds = this.getUserIdsByGroupId(groupId);
        //标记删除群组
        boolean result = groupDomainService.deleteGroup(groupId, session.getUserId());
        //群组标记删除成功
        if (result) {
            //删除群成员
            groupMemberDomainService.removeMemberByGroupId(groupId);
            logger.info("GroupServiceImpl.deleteGroup | 删除群聊 | 群聊ID: {}", groupId);
            GroupEvent groupEvent = new GroupEvent(groupId, session.getUserId(), PlatformConstants.GROUP_HANDLER_DELETE, this.getTopicEvent(), userIds);
            messageEventSenderService.send(groupEvent);
        } else {
            logger.info("GroupServiceImpl.deleteGroup | 删除群聊失败 | 群聊ID: {}", groupId);
        }
    }

    @Override
    public void quitGroup(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        //验证是否可执行退群操作
        if (!groupDomainService.quitGroup(groupId, session.getUserId())) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "不可退出群组");
        }
        if (groupMemberDomainService.removeMember(session.getUserId(), groupId)) {
            logger.info("GroupServiceImpl.quitGroup | 退群成功 | 用户ID: {}, 群聊ID: {}", session.getUserId(), groupId);

            GroupEvent groupEvent = new GroupEvent(groupId, session.getUserId(), PlatformConstants.GROUP_HANDLER_QUIT, this.getTopicEvent(), Collections.emptyList());
            messageEventSenderService.send(groupEvent);
        }
    }

    @Override
    public void kickGroup(Long groupId, Long userId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        if (!groupDomainService.kickGroup(groupId, userId, session.getUserId())) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "踢人异常");
        }
        if (groupMemberDomainService.removeMember(userId, groupId)) {
            logger.info("GroupServiceImpl.kickGroup | 群主踢人成功 | 群主ID: {}, 被踢用户ID: {}, 群聊ID: {}", session.getUserId(), userId, groupId);

            GroupEvent groupEvent = new GroupEvent(groupId, userId, PlatformConstants.GROUP_HANDLER_KICK, this.getTopicEvent(), Collections.emptyList());
            messageEventSenderService.send(groupEvent);
        }
    }

    @Override
    public List<GroupVO> findGroups() {
        return distributeCacheService.queryWithPassThroughList(
                PlatformConstants.PLATFORM_REDIS_GROUP_LIST_KEY,
                SessionContext.getUserSession().getUserId(),
                GroupVO.class,
                groupDomainService::getGroupVOListByUserId,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES);
    }

    @Override
    public void invite(GroupInviteVO vo) {
        if (vo == null || CollectionUtil.isEmpty(vo.getFriendIds())) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        String groupName = groupDomainService.getGroupName(vo.getGroupId());
        if (StrUtil.isEmpty(groupName)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群聊不存在");
        }
        //获取群组现有群成员,去掉已经移除群的
        List<GroupMember> members = groupMemberDomainService.getGroupMemberListByGroupId(vo.getGroupId());
        long size = CollectionUtil.isEmpty(members) ? 0 : members.size();
        //一个群最多500人
        if (vo.getFriendIds().size() + size > Constants.MAX_GROUP_MEMBER_COUNT) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "群聊人数不能大于" + Constants.MAX_GROUP_MEMBER_COUNT + "人");
        }
        UserSession session = SessionContext.getUserSession();
        //获取好友数据
        List<Friend> finalFriendList = friendDubboService.getFriendByUserId(session.getUserId());
        if (finalFriendList == null) {
            finalFriendList = Collections.emptyList();
        }
        Map<Long, Friend> finalFriendMap = finalFriendList.stream().collect(Collectors.toMap(Friend::getFriendId, f -> f));
        List<Friend> userFriendList = vo.getFriendIds().stream()
                .map(finalFriendMap::get)
                .filter(Objects::nonNull)
                .toList();
        if (userFriendList.size() != vo.getFriendIds().size()) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "部分用户不是您的好友，邀请失败");
        }
        //保存或者更新成功
        if (groupMemberDomainService.saveGroupMemberList(this.getGroupMemberList(vo, groupName, members, userFriendList))) {
            logger.info("GroupServiceImpl.invite | 邀请好友进群 | 群聊ID: {}, 群聊名称: {}, 被邀请用户ID: {}", vo.getGroupId(), groupName, vo.getFriendIds());

            vo.getFriendIds().forEach(id -> {
                GroupEvent groupEvent = new GroupEvent(vo.getGroupId(), id, PlatformConstants.GROUP_HANDLER_INVITE, this.getTopicEvent(), Collections.emptyList());
                messageEventSenderService.send(groupEvent);
            });
        }
    }

    private List<GroupMember> getGroupMemberList(GroupInviteVO vo, String groupName, List<GroupMember> members, List<Friend> userFriendList) {
        Set<Long> existingMemberIds = members.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());
        Date now = new Date();
        return userFriendList.stream()
                .filter(friend -> !existingMemberIds.contains(friend.getFriendId()))
                .map(friend -> {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
                    groupMember.setGroupId(vo.getGroupId());
                    groupMember.setUserId(friend.getFriendId());
                    groupMember.setCreatedTime(now);
                    groupMember.setQuit(false);
                    groupMember.setAliasName(friend.getFriendNickName());
                    groupMember.setRemark(groupName);
                    groupMember.setHeadImage(friend.getFriendHeadImage());
                    return groupMember;
                }).toList();
    }

    @Override
    public Group getById(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_GROUP_SINGLE_KEY,
                groupId,
                Group.class,
                groupDomainService::getGroupById,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES);
    }

    @Override
    public GroupVO findById(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        GroupVO groupVO = distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_GROUP_VO_SINGLE_KEY,
                new GroupParams(SessionContext.getUserSession().getUserId(), groupId),
                GroupVO.class,
                groupDomainService::getGroupVOByParams,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        if (groupVO == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "您未加入群聊");
        }
        return groupVO;
    }

    @Override
    public List<GroupMemberVO> findGroupMembers(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return distributeCacheService.queryWithPassThroughList(
                PlatformConstants.PLATFORM_REDIS_MEMBER_VO_LIST_KEY,
                groupId,
                GroupMemberVO.class,
                this::getGroupMemberVOS,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES);
    }

    @Override
    public GroupMemberSimpleVO getGroupMemberSimpleVO(GroupParams groupParams) {
        if (groupParams == null || groupParams.isEmpty()) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_MEMBER_VO_SIMPLE_KEY,
                groupParams,
                GroupMemberSimpleVO.class,
                groupMemberDomainService::getGroupMemberSimpleVO,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public List<Long> getUserIdsByGroupId(Long groupId) {
        if (groupId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return distributeCacheService.queryWithPassThroughList(
                PlatformConstants.PLATFORM_REDIS_MEMBER_ID_KEY,
                groupId,
                Long.class,
                groupMemberDomainService::getUserIdsByGroupId,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES);
    }

    @Override
    public List<GroupMemberSimpleVO> getGroupMemberSimpleVOList(Long userId) {
        if (userId == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        return distributeCacheService.queryWithPassThroughList(
                PlatformConstants.PLATFORM_REDIS_MEMBER_LIST_SIMPLE_KEY,
                userId,
                GroupMemberSimpleVO.class,
                groupMemberDomainService::getGroupMemberSimpleVOList,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public boolean updateHeadImgByUserId(String headImg, Long userId) {
        return groupMemberDomainService.updateHeadImgByUserId(headImg, userId);
    }

    @Override
    public List<Long> getGroupIdsByUserId(Long userId) {
        List<GroupMemberSimpleVO> list = this.getGroupMemberSimpleVOList(userId);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map((GroupMemberSimpleVO::getGroupId)).toList();
    }

    @NotNull
    private List<GroupMemberVO> getGroupMemberVOS(Long groupId) {
        List<GroupMemberVO> memberList = groupMemberDomainService.getGroupMemberVoListByGroupId(groupId);
        List<Long> userList = memberList.stream().map(GroupMemberVO::getUserId).toList();
        List<Long> onlineUserIdList = client.getOnlineUserList(userList);

        return memberList.stream().peek(m -> m.setOnline(onlineUserIdList.contains(m.getUserId())))
                .sorted((m1, m2) -> m2.getOnline().compareTo(m1.getOnline()))
                .toList();
    }

    private String getTopicEvent() {
        return PlatformConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ?
                PlatformConstants.TOPIC_EVENT_ROCKETMQ_GROUP :
                PlatformConstants.TOPIC_EVENT_LOCAL;
    }
}
