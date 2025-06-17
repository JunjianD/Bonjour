package com.djj.bj.application.netty.runner;

import cn.hutool.core.collection.CollectionUtil;
import com.djj.bj.application.netty.NettyServer;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Netty服务器运行器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.runner
 * @className ServerRunner
 * @date 2025/6/15 21:30
 */
@Component
public class ServerRunner implements CommandLineRunner {
    @Resource
    private List<NettyServer> nettyServers;

    /**
     * 判断所有Netty服务器是否就绪
     */
    public boolean isReady() {
        for (NettyServer server : nettyServers) {
            if (!server.isReady()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void run(String... args) throws Exception {
        if(!CollectionUtil.isEmpty(nettyServers)) {
            // 启动每个Netty服务器
            nettyServers.forEach(NettyServer::start);
        }
    }

    @PreDestroy
    public void destroy() {
        if (!CollectionUtil.isEmpty(nettyServers)) {
            // 停止每个Netty服务器
            nettyServers.forEach(NettyServer::shutdown);
        }
    }
}
