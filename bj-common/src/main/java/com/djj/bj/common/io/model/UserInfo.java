package com.djj.bj.common.io.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 保存用户信息，包括用户id以及登陆终端类型
 *
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className UserInfo
 * @author jj_D
 * @date 2025/5/26 16:01
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 终端类型
     */
    private Integer terminalType;
}
