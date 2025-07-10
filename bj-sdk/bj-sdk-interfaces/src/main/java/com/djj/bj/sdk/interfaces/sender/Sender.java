package com.djj.bj.sdk.interfaces.sender;

import com.djj.bj.common.io.enums.TerminalType;
import com.djj.bj.common.io.model.GroupChat;
import com.djj.bj.common.io.model.PrivateChat;

import java.util.List;
import java.util.Map;

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

    /**
     * 发送群聊消息
     *
     * @param groupChat 群聊消息对象
     * @param <T>       消息内容类型
     */
    <T> void sendGroupMessage(GroupChat<T> groupChat);

    /**
     * 获取用户对应在线终端映射
     *
     * @param userIds 用户ID列表
     * @return 用户ID与在线终端类型的映射
     */
    Map<Long, List<TerminalType>> getOnlineTerminalMap(List<Long> userIds);

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return true 如果用户在线，false 如果用户不在线
     */
    Boolean isOnline(Long userId);

    /**
     * 获取在线用户ID列表
     *
     * @return 在线用户ID列表
     */
    List<Long> getOnlineUserIds(List<Long> userIds);
}
