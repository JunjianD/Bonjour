package com.djj.bj.sdk.interfaces.sender;

import com.djj.bj.common.io.model.PrivateChat;

/**
 * 消息发送器接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.interfaces.sender
 * @interfaceName Sender
 * @date 2025/7/9 16:17
 */
public interface Sender {
    /**
     * 发送私聊消息
     *
     * @param privateChat 私聊消息对象
     * @param <T>         消息内容类型
     */
    <T> void sendPrivateMessage(PrivateChat<T> privateChat);
}
