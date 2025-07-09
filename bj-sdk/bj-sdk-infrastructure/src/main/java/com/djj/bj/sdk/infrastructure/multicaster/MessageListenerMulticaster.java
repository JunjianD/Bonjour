package com.djj.bj.sdk.infrastructure.multicaster;

import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.model.SendResult;

/**
 * 广播消息接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.infrastructure.multicaster
 * @className MessageListenerMulticaster
 * @date 2025/7/9 16:11
 */
public interface MessageListenerMulticaster {
    /**
     * 广播消息
     *
     * @param listeningType 监听类型
     * @param result 发送的结果
     * @param <T> 泛型消息类型
     */
    <T> void multicast(ListeningType listeningType, SendResult<T> result);
}
