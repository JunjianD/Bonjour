package com.djj.bj.server.application.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import com.djj.bj.server.application.netty.processor.MessageProcessor;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.model.HeartbeatInfo;
import com.djj.bj.common.io.model.SendMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 心跳处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.processor.impl
 * @className HeartbeatProcessor
 * @date 2025/7/6 11:34
 */
@Component
public class HeartbeatProcessor implements MessageProcessor<HeartbeatInfo> {
    @Resource
    private DistributeCacheService distributeCacheService;

    @Value("${heartbeat.count}")
    private Integer heartbeatCount;

    @Override
    public void process(ChannelHandlerContext ctx, HeartbeatInfo data) {
        // 响应websocket心跳
        this.responseWS(ctx);
        // 设置属性
        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf(Constants.HEARTBEAT_COUNTS);
        Long heartbeatCounts = ctx.channel().attr(heartBeatAttr).get();
        ctx.channel().attr(heartBeatAttr).set(++heartbeatCounts);
        if (heartbeatCounts % heartbeatCount == 0) {
            // 心跳n次，用户在线状态续命一次
            AttributeKey<Long> userIdAttr = AttributeKey.valueOf(Constants.USER_ID);
            Long userId = ctx.channel().attr(userIdAttr).get();
            AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(Constants.TERMINAL_TYPE);
            Integer terminal = ctx.channel().attr(terminalAttr).get();
            String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, userId.toString(), terminal.toString());
            distributeCacheService.expire(redisKey, Constants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    private void responseWS(ChannelHandlerContext ctx) {
        // 响应websocket数据
        SendMessage<?> sendMessage = new SendMessage<>();
        sendMessage.setSystemInfo(SystemInfoType.HEARTBEAT.getCode());
        ctx.channel().writeAndFlush(sendMessage);
    }

    @Override
    public HeartbeatInfo transForm(Object obj) {
        Map<?, ?> map = (Map<?, ?>) obj;
        return BeanUtil.fillBeanWithMap(map,new HeartbeatInfo(),false);
    }
}
