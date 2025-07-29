package com.djj.bj.platform.user.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.enums.TerminalType;
import com.djj.bj.common.io.jwt.JwtUtils;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.jwt.JwtProperties;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.LoginDTO;
import com.djj.bj.platform.common.model.dto.ModifyPwdDTO;
import com.djj.bj.platform.common.model.dto.RegisterDTO;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.vo.LoginVO;
import com.djj.bj.platform.common.model.vo.OnlineTerminalVO;
import com.djj.bj.platform.common.model.vo.UserVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.user.application.service.UserService;
import com.djj.bj.platform.user.domain.service.UserDomainService;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.service.impl
 * @className UserServiceImpl
 * @date 2025/7/22 21:34
 */
@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDomainService userDomainService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private Client client;

    @Override
    public LoginVO login(LoginDTO dto) {
        if (dto == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        User user = distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_USER_KEY,
                dto.getUserName(),
                User.class,
                userDomainService::getUserByUserName,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        if (user == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "当前用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BJException(HttpCode.PASSWORD_ERROR, "用户名或密码错误");
        }
        UserSession userSession = BeanUtils.copyProperties(user, UserSession.class);
        userSession.setUserId(user.getId());
        userSession.setTerminalType(dto.getTerminal());
        String strJson = JSON.toJSONString(userSession);
        String accessToken = JwtUtils.sign(
                user.getId(),
                strJson,
                jwtProperties.getAccessTokenExpireIn(),
                jwtProperties.getAccessTokenSecret()
        );
        String refreshToken = JwtUtils.sign(
                user.getId(),
                strJson,
                jwtProperties.getRefreshTokenExpireIn(),
                jwtProperties.getRefreshTokenSecret()
        );
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setAccessTokenExpireTime(jwtProperties.getAccessTokenExpireIn());
        loginVO.setRefreshToken(refreshToken);
        loginVO.setRefreshTokenExpireTime(jwtProperties.getRefreshTokenExpireIn());
        return loginVO;
    }

    @Override
    public void register(RegisterDTO dto) {
        if (dto == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        User user = distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_USER_KEY,
                dto.getUserName(),
                User.class,
                userDomainService::getUserByUserName,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        if (user != null) {
            throw new BJException(HttpCode.USERNAME_ALREADY_REGISTER);
        }
        user = BeanUtils.copyProperties(dto, User.class);
        user.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        user.setCreatedTime(new Date());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userDomainService.saveOrUpdateUser(user);
        logger.info("用户注册成功，用户id:{},用户名:{},昵称:{}", user.getId(), dto.getUserName(), dto.getNickName());
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        // 验证token
        if (!JwtUtils.checkSign(refreshToken, jwtProperties.getRefreshTokenSecret())) {
            throw new BJException("refreshToken无效或已过期");
        }
        String strJson = JwtUtils.getInfo(refreshToken);
        Long userId = JwtUtils.getUserId(refreshToken);
        String accessToken = JwtUtils.sign(userId, strJson, jwtProperties.getAccessTokenExpireIn(), jwtProperties.getAccessTokenSecret());
        String newRefreshToken = JwtUtils.sign(userId, strJson, jwtProperties.getRefreshTokenExpireIn(), jwtProperties.getRefreshTokenSecret());
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setAccessTokenExpireTime(jwtProperties.getAccessTokenExpireIn());
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setRefreshTokenExpireTime(jwtProperties.getRefreshTokenExpireIn());
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyPassword(ModifyPwdDTO dto) {
        // 获取用户Session
        UserSession session = SessionContext.getUserSession();
        // 不从缓存中获取，防止缓存数据不一致
        User user = userDomainService.getById(session.getUserId());
        if (user == null) {
            throw new BJException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BJException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userDomainService.saveOrUpdateUser(user);
        logger.info("用户修改密码，用户id:{},用户名:{},昵称:{}", user.getId(), user.getUserName(), user.getNickName());
    }

    @Override
    public User findUserByUserName(String username) {
        User user = distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_USER_KEY,
                username,
                User.class,
                userDomainService::getUserByUserName,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        if (user == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "当前用户不存在");
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserVO vo) {
        UserSession session = SessionContext.getUserSession();
        if (!session.getUserId().equals(vo.getUserId())) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "只能更新自己的信息");
        }
        User user = userDomainService.getById(vo.getUserId());
        if (Objects.isNull(user)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "用户不存在");
        }
        // 如果用户更新了昵称和头像，则更新好友昵称和头像
        if (!user.getNickName().equals(vo.getNickName()) || !user.getHeadImageThumb().equals(vo.getHeadImageThumb())) {
            //TODO 后续完善
        }
        // 如果用户更新了昵称和头像，则更新群聊中的头像
        if (!user.getNickName().equals(vo.getNickName()) || !user.getHeadImageThumb().equals(vo.getHeadImageThumb())) {
            //TODO 后续完善
        }

        // 更新用户的基本信息
        if (!StrUtil.isEmpty(vo.getNickName())) {
            user.setNickName(vo.getNickName());
        }
        if (vo.getSex() != null) {
            user.setSex(vo.getSex());
        }
        if (!StrUtil.isEmpty(vo.getSignature())) {
            user.setSignature(vo.getSignature());
        }
        if (!StrUtil.isEmpty(vo.getHeadImage())) {
            user.setHeadImage(vo.getHeadImage());
        }
        if (!StrUtil.isEmpty(vo.getHeadImageThumb())) {
            user.setHeadImageThumb(vo.getHeadImageThumb());
        }
        userDomainService.saveOrUpdateUser(user);
    }

    @Override
    public UserVO findUserById(Long id, boolean constantsOnlineFlag) {
        User user = distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_USER_KEY,
                id,
                User.class,
                userDomainService::getById,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        if (user == null) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "用户不存在");
        }
        UserVO vo = BeanUtils.copyProperties(user, UserVO.class);
        if (constantsOnlineFlag) {
            vo.setOnline(client.isOnline(id));
        }
        return null;
    }

    @Override
    public User getUserById(Long userId) {
        return distributeCacheService.queryWithPassThrough(
                PlatformConstants.PLATFORM_REDIS_USER_KEY,
                userId,
                User.class,
                userDomainService::getById,
                PlatformConstants.DEFAULT_REDIS_CACHE_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public List<UserVO> findUserByName(String name) {
        List<User> userList = userDomainService.findUserByName(name);
        // TODO 调用Client的方法后处理在线状态
        if (CollectionUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        List<Long> userIds = userList.stream().map(User::getId).toList();
        List<Long> onlineUserIds = client.getOnlineUserList(userIds);
        return userList.stream().map(user -> {
            UserVO vo = BeanUtils.copyProperties(user, UserVO.class);
            vo.setOnline(onlineUserIds.contains(user.getId()));
            return vo;
        }).toList();
    }

    @Override
    public List<OnlineTerminalVO> getOnlineTerminals(String userIds) {
        List<Long> userIdList = Arrays.stream(userIds.split(",")).map(Long::parseLong).toList();
        Map<Long, List<TerminalType>> terminalMap = client.getOnlineTerminal(userIdList);
        return terminalMap.entrySet().stream().map(e -> new OnlineTerminalVO(
                e.getKey(),
                e.getValue().stream().map(TerminalType::getCode).toList()
        )).toList();
    }
}
