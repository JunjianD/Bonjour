package com.djj.bj.common.io.event;

import com.djj.bj.common.io.model.BasicMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 基础事件类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.event
 * @className BaseEvent
 * @date 2025/7/25 17:49
 */
@NoArgsConstructor
@Getter
@Setter
public class BaseEvent extends BasicMessage {
    private Long eventId; // 事件ID

    public BaseEvent(Long eventId, String destination) {
        super(destination);
        this.eventId = eventId;
    }
}
