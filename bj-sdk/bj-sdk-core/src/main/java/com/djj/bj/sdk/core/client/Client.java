package com.djj.bj.sdk.core.client;

import com.djj.bj.common.io.model.PrivateChat;

/**
 * 客户端
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.core.client
 * @interfaceName Client
 * @date 2025/7/9 17:20
 */
public interface Client {
    /**
     * 发送私聊消息
     *
     * @param privateChat 私聊消息
     * @param <T>         消息类型
     */
    <T> void sendPrivateMessage(PrivateChat<T> privateChat);
}
