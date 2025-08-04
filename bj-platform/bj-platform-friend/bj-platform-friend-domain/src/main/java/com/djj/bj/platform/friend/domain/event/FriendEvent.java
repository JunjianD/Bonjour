package com.djj.bj.platform.friend.domain.event;

import com.djj.bj.common.io.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 好友事件模型
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.domain.event
 * @className FriendEvent
 * @date 2025/8/2 15:28
 */
@NoArgsConstructor
@Getter
@Setter
public class FriendEvent extends BaseEvent {
    /**
     * 操作
     */
    private String handler;

    /**
     * 好友ID
     */
    private Long friendId;

    public FriendEvent(Long id, Long friendId, String handler, String destination) {
        super(id, destination);
        this.handler = handler;
        this.friendId = friendId;
    }
}
