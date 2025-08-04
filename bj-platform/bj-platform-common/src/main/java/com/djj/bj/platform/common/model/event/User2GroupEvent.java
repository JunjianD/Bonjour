package com.djj.bj.platform.common.model.event;

import com.djj.bj.common.io.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户微服务到群组微服务的事件
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.event
 * @className User2GroupEvent
 * @date 2025/8/5 01:10
 */
@NoArgsConstructor
@Getter
@Setter
public class User2GroupEvent extends BaseEvent {
    /**
     * 用户头像缩略图
     */
    private String headImageThumb;

    public User2GroupEvent(Long id, String headImageThumb, String destination) {
        super(id, destination);
        this.headImageThumb = headImageThumb;
    }
}
