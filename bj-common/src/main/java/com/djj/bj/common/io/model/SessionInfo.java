package com.djj.bj.common.io.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 保存登陆后的session，包括用户id以及登陆终端类型
 *
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className SessionInfo
 * @author jj_D
 * @date 2025/5/26 15:51
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 终端类型
     */
    private Integer terminalType;
}
