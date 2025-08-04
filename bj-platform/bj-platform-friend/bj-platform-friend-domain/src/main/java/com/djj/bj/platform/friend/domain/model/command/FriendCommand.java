package com.djj.bj.platform.friend.domain.model.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 好友关系
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.domain.model.command
 * @className FriendCommand
 * @date 2025/8/1 15:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FriendCommand {
    private Long userId;
    private Long friendId;

    public boolean isEmpty() {
        return userId == null || friendId == null;
    }
}
