package com.djj.bj.platform.common.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * WebRTC会话类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.session
 * @className WebrtcSession
 * @date 2025/8/10 20:45
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WebrtcSession {
    /**
     * 发起者id
     */
    private Long callerId;
    /**
     * 发起者终端类型
     */
    private Integer callerTerminal;

    /**
     * 接受者id
     */
    private Long acceptorId;

    /**
     * 接受者终端类型
     */
    private Integer acceptorTerminal;
}
