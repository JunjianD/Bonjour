package com.djj.bj.server.application.netty.handler;

import com.djj.bj.server.application.netty.cache.UserChannelCtxCache;
import com.djj.bj.server.application.netty.processor.MessageProcessor;
import com.djj.bj.server.application.netty.processor.factory.ProcessorFactory;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.common.io.model.SendMessage;
import com.djj.bj.server.infrastructure.holder.SpringContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通道处理器模块
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.handler
 * @className ChannelHandlerImpl
 * @date 2025/6/17 21:13
 */
public class ChannelHandlerImpl extends SimpleChannelInboundHandler<SendMessage<?>> {
    private final Logger logger = LoggerFactory.getLogger(ChannelHandlerImpl.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SendMessage sendMessage) throws Exception {
        MessageProcessor messageProcessor = ProcessorFactory.getProcessor(SystemInfoType.fromCode(sendMessage.getSystemInfo()));
        messageProcessor.process(channelHandlerContext, messageProcessor.transForm(sendMessage.getContent()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 通道添加时的处理逻辑
        logger.info("ChannelHandlerImpl.handlerAdded: Channel added: {}", ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(Constants.USER_ID);
        Long userId = ctx.channel().attr(userIdAttr).get();

        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(Constants.TERMINAL_TYPE);
        Integer terminal = ctx.channel().attr(terminalAttr).get();

        ChannelHandlerContext channelCtx = UserChannelCtxCache.getCtx(userId, terminal);
        // 防止异地登录误删
        if (channelCtx != null && channelCtx.channel().id().equals(ctx.channel().id())) {
            UserChannelCtxCache.removeCtx(userId, terminal);
            DistributeCacheService distributeCacheService = SpringContextHolder.getBean(Constants.DISTRIBUTED_CACHE_REDIS_SERVICE_KEY);
            String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.USER_SERVER_ID, userId.toString(), terminal.toString());
            distributeCacheService.delete(redisKey);
            logger.info("ChannelHandlerImpl.handlerRemoved: Disconnected, userId: {}, terminal: {}", userId, terminal);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ChannelHandlerImpl.exceptionCaught: Exception occurred: {}", cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                AttributeKey<Long> attr = AttributeKey.valueOf(Constants.USER_ID);
                Long userId = ctx.channel().attr(attr).get();

                AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(Constants.TERMINAL_TYPE);
                Integer terminal = ctx.channel().attr(terminalAttr).get();
                logger.info("ChannelHandlerImpl.userEventTriggered: Heartbeat timeout, disconnecting, userId: {}, terminal: {}", userId, terminal);
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
