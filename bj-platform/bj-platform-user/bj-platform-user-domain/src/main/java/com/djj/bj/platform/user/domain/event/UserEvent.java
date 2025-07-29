package com.djj.bj.platform.user.domain.event;

import com.djj.bj.common.io.event.BaseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户领域事件类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.domain.event
 * @className UserEvent
 * @date 2025/7/25 17:48
 */
@Getter
@Setter
public class UserEvent extends BaseEvent {
    private String userName;

    public UserEvent(Long eventId, String userName, String destination) {
        super(eventId, destination);
        this.userName = userName;
    }
}
