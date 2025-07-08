package com.djj.bj.server.application.netty.websocket;

import com.djj.bj.server.application.netty.NettyServer;
import com.djj.bj.server.application.netty.handler.ChannelHandlerImpl;
import com.djj.bj.server.application.netty.websocket.codec.WebSocketMessageProtocolDecoder;
import com.djj.bj.server.application.netty.websocket.codec.WebSocketMessageProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


import java.util.concurrent.TimeUnit;

/**
 * WebSocketServer 接口实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.websocket
 * @className WebSocketServer
 * @date 2025/6/15 16:55
 */
@Component
@ConditionalOnProperty(prefix = "websocket", value = "enable", havingValue = "true", matchIfMissing = true)
public class WebSocketServer implements NettyServer {
    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private volatile boolean ready = false;

    @Value("${websocket.port}")
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
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加自定义的编码器、解码器和处理器
                        pipeline.addLast(new IdleStateHandler(120,0,0, TimeUnit.SECONDS));
                        pipeline.addLast("http-codec", new HttpServerCodec());
                        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65535)); // 聚合HTTP消息
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler()); // 支持大文件传输
                        pipeline.addLast("websocket-handler", new WebSocketServerProtocolHandler("/ws")); // WebSocket协议处理器
                        pipeline.addLast("encode", new WebSocketMessageProtocolEncoder()); // 替换为实际的编码器
                        pipeline.addLast("decode", new WebSocketMessageProtocolDecoder()); // 替换为实际的解码器
                        pipeline.addLast("handler", new ChannelHandlerImpl()); // 替换为实际的处理器
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 5) // 设置服务端用于临时存放的已完成三次握手的请求的队列大小
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            bootstrap.bind(port).sync().channel();
            this.ready = true;
            logger.info("WebSocketServer started at port: {}", port);
        }catch (InterruptedException e) {
            logger.error("WebSocketServer start error: {}", e.getMessage());
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
        logger.info("WebSocketServer shutdown");
    }
}
