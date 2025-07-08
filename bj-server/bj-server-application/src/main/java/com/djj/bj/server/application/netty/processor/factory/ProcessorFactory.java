package com.djj.bj.server.application.netty.processor.factory;

import com.djj.bj.server.application.netty.processor.MessageProcessor;
import com.djj.bj.server.application.netty.processor.impl.GroupMessageProcessor;
import com.djj.bj.server.application.netty.processor.impl.HeartbeatProcessor;
import com.djj.bj.server.application.netty.processor.impl.LoginProcessor;
import com.djj.bj.server.application.netty.processor.impl.PrivateMessageProcessor;
import com.djj.bj.common.io.enums.SystemInfoType;
import com.djj.bj.server.infrastructure.holder.SpringContextHolder;

/**
 * 处理器工厂类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.processor.factory
 * @className ProcessorFactory
 * @date 2025/6/18 23:23
 */
public class ProcessorFactory {
    public static MessageProcessor<?> getProcessor(SystemInfoType systemInfoType) {
        return switch (systemInfoType) {
            // 登录
            case LOGIN -> SpringContextHolder.getApplicationContext().getBean(LoginProcessor.class);
            // 心跳
            case HEARTBEAT -> SpringContextHolder.getApplicationContext().getBean(HeartbeatProcessor.class);
            // 单聊消息
            case PRIVATE_CHAT -> SpringContextHolder.getApplicationContext().getBean(PrivateMessageProcessor.class);
            // 群聊消息
            case GROUP_CHAT -> SpringContextHolder.getApplicationContext().getBean(GroupMessageProcessor.class);
            // 其他类型
            default -> null;
        };
    }
}
