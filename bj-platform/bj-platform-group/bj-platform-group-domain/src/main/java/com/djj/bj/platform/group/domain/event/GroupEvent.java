package com.djj.bj.platform.group.domain.event;

import com.djj.bj.common.io.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 群组事件类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.event
 * @className GroupEvent
 * @date 2025/8/5 00:22
 */
@NoArgsConstructor
@Getter
@Setter
public class GroupEvent extends BaseEvent {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 操作类型
     */
    private String handler;

    /**
     * 成员id列表, 用于批量操作，只有解散群组时使用
     */
    private List<Long> memberIdList;

    public GroupEvent(Long id, Long userId, String handler, String destination, List<Long> memberIdList) {
        super(id, destination);
        this.userId = userId;
        this.handler = handler;
        this.memberIdList = memberIdList;
    }
}
