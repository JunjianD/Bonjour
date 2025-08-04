package com.djj.bj.platform.common.model.event;

import com.djj.bj.common.io.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户微服务到好友微服务的事件
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.event
 * @className User2FriendEvent
 * @date 2025/8/2 17:21
 */
@NoArgsConstructor
@Getter
@Setter
public class User2FriendEvent extends BaseEvent {
    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String headImg;

    public User2FriendEvent(Long id, String nickName, String headImg, String destination) {
        super(id, destination);
        this.nickName = nickName;
        this.headImg = headImg;
    }
}
