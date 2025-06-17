package com.djj.bj.application.netty.tcpsocket;

import com.djj.bj.application.netty.NettyServer;
import com.djj.bj.application.netty.handler.ChannelHandlerImpl;
import com.djj.bj.application.netty.tcpsocket.codec.TcpSocketMessageProtocolDecoder;
import com.djj.bj.application.netty.tcpsocket.codec.TcpSocketMessageProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * TCP Socket服务器实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.tcpsocket
 * @className TcpSocketServer
 * @date 2025/6/15 16:02
 */
@Component
@ConditionalOnProperty(prefix = "tcpsocket", value = "enable", havingValue = "true", matchIfMissing = true)
public class TcpSocketServer implements NettyServer {
    private final Logger logger = LoggerFactory.getLogger(TcpSocketServer.class);

    private volatile boolean ready = false;

    @Value("${tcpsocket.port}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;


    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // 在这里添加管道处理器
                        // ch.pipeline().addLast(new YourHandler());
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加自定义的编码器、解码器和处理器
                        pipeline.addLast(new IdleStateHandler(120,0,0, TimeUnit.SECONDS));
                        pipeline.addLast("encode", new TcpSocketMessageProtocolEncoder()); // 替换为实际的编码器
                        pipeline.addLast("decode", new TcpSocketMessageProtocolDecoder()); // 替换为实际的解码器
                        pipeline.addLast("handler", new ChannelHandlerImpl()); // 替换为实际的处理器
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 5) // 设置服务端用于临时存放的已完成三次握手的请求的队列大小
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            bootstrap.bind(port).sync().channel();
            this.ready = true;
            logger.info("TcpSocketServer started at port: {}", port);
        }catch (InterruptedException e) {
            logger.error("TcpSocketServer start error: {}", e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        if(bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if(workGroup != null && !workGroup.isShuttingDown() && !workGroup.isShutdown()) {
            workGroup.shutdownGracefully();
        }
        this.ready = false;
        logger.info("TcpSocketServer shutdown");
    }
}
