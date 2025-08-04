package com.djj.bj.platform.group.application.cache.impl;

import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.params.GroupParams;
import com.djj.bj.platform.group.application.cache.GroupCacheService;
import com.djj.bj.platform.group.domain.event.GroupEvent;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    @Override
    public void updateGroupCache(GroupEvent groupEvent) {
        //TODO 目前先全部清除缓存，后续优化
        if (groupEvent == null) {
            return;
        }
        switch (groupEvent.getHandler()) {
            case PlatformConstants.GROUP_HANDLER_INVITE:
                this.handlerInvite(groupEvent);
                break;
            case PlatformConstants.GROUP_HANDLER_KICK:
                this.handlerKick(groupEvent);
                break;
            case PlatformConstants.GROUP_HANDLER_QUIT:
                this.handlerQuit(groupEvent);
                break;
            case PlatformConstants.GROUP_HANDLER_DELETE:
                this.handlerDelete(groupEvent);
                break;
            case PlatformConstants.GROUP_HANDLER_MODIFY:
                this.handlerModify(groupEvent);
                break;
            case PlatformConstants.GROUP_HANDLER_CREATE:
                this.handlerCreate(groupEvent);
                break;
            default:
                logger.info("GroupCacheServiceImpl.updateGroupCache | 不存在此类事件 | 群组缓存服务接收到的事件参数为|{}", JSONObject.toJSONString(groupEvent));
        }
    }

    private void handlerInvite(GroupEvent groupEvent) {
        logger.info("groupCacheService|进入邀人事件处理|{}", JSONObject.toJSONString(groupEvent));
        this.handlerGroupMember(groupEvent);
    }

    private void handlerKick(GroupEvent groupEvent) {
        logger.info("groupCacheService|进入踢人事件处理|{}", JSONObject.toJSONString(groupEvent));
        this.handlerGroupMember(groupEvent);
    }

    private void handlerQuit(GroupEvent groupEvent) {
        logger.info("groupCacheService|进入退群事件处理|{}", JSONObject.toJSONString(groupEvent));
        this.handlerGroupMember(groupEvent);
    }

    private void handlerDelete(GroupEvent groupEvent) {
        logger.info("groupCacheService|进入解散群事件处理|{}", JSONObject.toJSONString(groupEvent));
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_VO_SINGLE_KEY, new GroupParams(groupEvent.getUserId(), groupEvent.getEventId()));
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_LIST_KEY, groupEvent.getUserId());
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_SINGLE_KEY, groupEvent.getEventId());
        distributeCacheService.delete(redisKey);
        //群成员自动过期即可
    }

    private void handlerModify(GroupEvent groupEvent) {
        logger.info("groupCacheService|进入修改群事件处理|{}", JSONObject.toJSONString(groupEvent));
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_VO_SINGLE_KEY, new GroupParams(groupEvent.getUserId(), groupEvent.getEventId()));
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_SINGLE_KEY, groupEvent.getEventId());
        distributeCacheService.delete(redisKey);
    }

    private void handlerCreate(GroupEvent groupEvent) {
        logger.info("groupCacheService|进入保存群组事件处理|{}", JSONObject.toJSONString(groupEvent));
    }

    private void handlerGroupMember(GroupEvent groupEvent) {
        String redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_VO_LIST_KEY, groupEvent.getEventId());
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_VO_SIMPLE_KEY, new GroupParams(groupEvent.getUserId(), groupEvent.getEventId()));
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_ID_KEY, groupEvent.getEventId());
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_MEMBER_LIST_SIMPLE_KEY, groupEvent.getUserId());
        distributeCacheService.delete(redisKey);

        redisKey = distributeCacheService.getKey(PlatformConstants.PLATFORM_REDIS_GROUP_LIST_KEY, groupEvent.getUserId());
        distributeCacheService.delete(redisKey);
    }
}
