package com.djj.bj.platform.common.session;

import com.djj.bj.common.io.model.SessionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 用户会话信息类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.session
 * @className UserSession
 * @date 2025/7/14 15:49
 */
@NoArgsConstructor
@Getter
public class UserSession extends SessionInfo {
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    public UserSession(Long userId, Integer terminal, String userName, String nickName) {
        super(userId, terminal);
        this.userName = userName;
        this.nickName = nickName;
    }
}
