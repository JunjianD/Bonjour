package com.djj.bj.server.application.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.jwt.JwtUtils;
import com.djj.bj.common.io.model.LoginInfo;
import com.djj.bj.common.io.model.SendMessage;
import com.djj.bj.common.io.model.SessionInfo;
import com.djj.bj.server.application.netty.cache.UserChannelCtxCache;
import com.djj.bj.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录处理器实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.server.application.netty.processor.impl
 * @className LoginProcessor
 * @date 2025/6/18 21:15
 */
@Component
public class LoginProcessor implements MessageProcessor<LoginInfo> {
    private final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    @Value("${jwt.accessToken.secret}")
    private String accessTokenSecret;

    @Value("${server.id}")
    private Long serverId;

    @Resource
    private DistributeCacheService distributeCacheService;

    @Override
    public synchronized void process(ChannelHandlerContext ctx, LoginInfo loginInfo) {
        // 登录Token检验未通过
        if (!JwtUtils.checkSign(loginInfo.getAccessToken(), accessTokenSecret)) {
            ctx.channel().close();
            logger.warn("LoginProcessor.process|用户登录信息校验未通过,强制下线,token:{}", loginInfo.getAccessToken());
            return;
        }

        String info = JwtUtils.getInfo(loginInfo.getAccessToken());
        SessionInfo sessionInfo = JSON.parseObject(info, SessionInfo.class);
        if(sessionInfo == null) {
            logger.warn("LoginProcessor.process|转化后的SessionInfo为空");
            return;
        }

        // 获取用户ID和终端类型
        Long userId = sessionInfo.getUserId();
        Integer terminalType = sessionInfo.getTerminalType();
        logger.info("LoginProcessor.process|用户登录,userId:{},terminalType:{}", userId, terminalType);
        ChannelHandlerContext channelCtx = UserChannelCtxCache.getCtx(userId,terminalType);

        // 判断当前连接的ID不同，则表示当前用户已经在异地登录
        if(channelCtx != null && !channelCtx.channel().id().equals(ctx.channel().id())) {
            // 存在另一个相同设备的登录，踢出原先的连接
            SendMessage<String> sendMessage = new SendMessage<>(SystemInfoType.FORCE_LOGOUT.getCode(),"您已在其他地方登录，将被强制下线");
            channelCtx.channel().writeAndFlush(sendMessage);
            logger.info("LoginProcessor.process|用户异地登录,强制下线, userId:{}, terminalType:{}", userId, terminalType);
        }

        // 缓存用户和Channel的关系
        UserChannelCtxCache.addCtx(userId, terminalType, ctx);
        // 设置用户相关的属性
        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(Constants.USER_ID);
        ctx.channel().attr(userIdAttr).set(userId);

        // 设置用户的终端类型
        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(Constants.TERMINAL_TYPE);
        ctx.channel().attr(terminalAttr).set(terminalType);

        // 初始化心跳的次数
        AttributeKey<Long> heartbeatAttr = AttributeKey.valueOf(Constants.HEARTBEAT_COUNTS);
        ctx.channel().attr(heartbeatAttr).set(0L);

        // 缓存用户的服务器信息
        String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, userId.toString(), terminalType.toString());
        distributeCacheService.set(redisKey, serverId, Constants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // 响应登录成功
        SendMessage<?> sendMessage = new SendMessage<>();
        sendMessage.setSystemInfo(SystemInfoType.LOGIN.getCode());
        ctx.channel().writeAndFlush(sendMessage);
    }

    @Override
    public LoginInfo transForm(Object obj) {
        // 将对象转换为LoginInfo
        Map<?,?> map = (Map<?, ?>) obj;
        return BeanUtil.fillBeanWithMap(map, new LoginInfo(), false);
    }
}
