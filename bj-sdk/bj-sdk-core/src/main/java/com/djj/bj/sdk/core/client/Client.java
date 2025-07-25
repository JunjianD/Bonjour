package com.djj.bj.sdk.core.client;

import com.djj.bj.common.io.enums.TerminalType;
import com.djj.bj.common.io.model.GroupChat;
import com.djj.bj.common.io.model.PrivateChat;

import java.util.List;
import java.util.Map;

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

    /**
     * 发送群聊消息
     *
     * @param groupChat 群聊消息
     * @param <T>       消息类型
     */
    <T> void sendGroupMessage(GroupChat<T> groupChat);

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return true 在线，false 不在线
     */
    Boolean isOnline(Long userId);

    /**
     * 获取在线用户列表
     *
     * @param userIds 用户ID列表
     * @return 在线用户ID列表
     */
    List<Long> getOnlineUserList(List<Long> userIds);

    /**
     * 获取用户在线的终端列表
     *
     * @param userIds 用户ID列表
     * @return 用户ID与其在线终端类型的映射
     */
    Map<Long, List<TerminalType>> getOnlineTerminal(List<Long> userIds);
}
