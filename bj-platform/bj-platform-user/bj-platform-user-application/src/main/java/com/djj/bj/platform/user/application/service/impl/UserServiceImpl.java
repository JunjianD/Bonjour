package com.djj.bj.platform.user.application.service.impl;

import com.alibaba.fastjson2.JSON;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.cache.id.SnowFlakeFactory;
import com.djj.bj.common.io.jwt.JwtUtils;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.jwt.JwtProperties;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.LoginDTO;
import com.djj.bj.platform.common.model.dto.RegisterDTO;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.vo.LoginVO;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.common.utils.BeanUtils;
import com.djj.bj.platform.user.application.service.UserService;
import com.djj.bj.platform.user.domain.service.UserDomainService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
}
