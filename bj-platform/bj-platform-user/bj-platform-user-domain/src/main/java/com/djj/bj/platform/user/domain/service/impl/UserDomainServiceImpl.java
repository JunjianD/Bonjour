package com.djj.bj.platform.user.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.djj.bj.common.mq.event.MessageEventSenderService;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.user.domain.event.UserEvent;
import com.djj.bj.platform.user.domain.repository.UserRepository;
import com.djj.bj.platform.user.domain.service.UserDomainService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 领域层用户服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.domain.service.impl
 * @className UserDomainServiceImpl
 * @date 2025/7/22 19:41
 */
@Service
public class UserDomainServiceImpl extends ServiceImpl<UserRepository, User> implements UserDomainService {
    private final Logger logger = LoggerFactory.getLogger(UserDomainServiceImpl.class);

    @Value("${message.mq.event.type}")
    private String eventType;

    @Resource
    private MessageEventSenderService messageEventSenderService;

    @Override
    public User getUserByUserName(String userName) {
        if (StrUtil.isEmpty(userName)) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUserName, userName);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateUser(User user) {
        if (user == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        boolean result = this.saveOrUpdate(user);
        if (result) {
            //TODO 发布更新缓存事件
            logger.info("UserDomainServiceImpl.saveOrUpdateUser|用户信息更新成功, userId:{}", user.getUserId());
            UserEvent userEvent = new UserEvent(user.getUserId(), user.getUserName(), this.getTopicEvent());
            messageEventSenderService.send(userEvent);
            logger.info("UserDomainServiceImpl.saveOrUpdateUser|用户事件已经发布, userId:{}", user.getUserId());
        }
        return result;
    }

    private String getTopicEvent() {
        return PlatformConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ?
                PlatformConstants.TOPIC_EVENT_ROCKETMQ_USER :
                PlatformConstants.TOPIC_EVENT_LOCAL;
    }

    @Override
    public List<User> getUserListByName(String name) {
        if (StrUtil.isEmpty(name)) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(User::getUserName, name)
                .or().like(User::getNickName, name)
                .last("limit 20");
        return this.list(queryWrapper);
    }

    @Override
    public User getUserById(Long id) {
        return super.getById(id);
    }

    @Override
    public List<User> findUserByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(User::getUserName, name)
                .or().like(User::getNickName, name)
                .last("limit 20");
        List<User> userList = this.list(queryWrapper);
        if (CollectionUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return userList;
    }

}
