package com.djj.bj.sdk.core.client.impl;

import com.djj.bj.common.io.enums.TerminalType;
import com.djj.bj.common.io.model.GroupChat;
import com.djj.bj.common.io.model.PrivateChat;
import com.djj.bj.sdk.core.client.Client;
import com.djj.bj.sdk.interfaces.sender.Sender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 默认客户端实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.core.client.impl
 * @className DefaultClient
 * @date 2025/7/9 19:45
 */
@Service
public class DefaultClient implements Client {
    @Resource
    private Sender sender;


    @Override
    public <T> void sendPrivateMessage(PrivateChat<T> privateChat) {
        sender.sendPrivateMessage(privateChat);
    }

    @Override
    public <T> void sendGroupMessage(GroupChat<T> groupChat) {
        sender.sendGroupMessage(groupChat);
    }

    @Override
    public Boolean isOnline(Long userId) {
        return sender.isOnline(userId);
    }

    @Override
    public List<Long> getOnlineUserList(List<Long> userIds) {
        return sender.getOnlineUserIds(userIds);
    }

    @Override
    public Map<Long, List<TerminalType>> getOnlineTerminal(List<Long> userIds) {
        return sender.getOnlineTerminalMap(userIds);
    }
}
