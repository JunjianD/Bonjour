package com.djj.bj.application.netty;

/**
 * NettyServer 接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty
 * @interfaceName NettyServer
 * @date 2025/6/15 14:52
 */
public interface NettyServer {
    /**
     * 服务是否已经就绪
     */
    boolean isReady();

    /**
     * 启动Netty服务器
     */
    void  start();

    /**
     * 停止Netty服务器
     */
    void shutdown();
}
