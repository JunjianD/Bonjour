package com.djj.bj.server.application.netty.processor;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理器接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.processor
 * @interfaceName MessageProcessor
 * @date 2025/6/18 21:13
 */
public interface MessageProcessor<T> {

    default void process(T data) {
    }

    default void process(ChannelHandlerContext ctx, T data) {
    }

    default T transForm(Object obj) {
        return (T) obj;
    }

}
