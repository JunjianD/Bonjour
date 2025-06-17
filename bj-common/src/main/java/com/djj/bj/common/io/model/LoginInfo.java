package com.djj.bj.common.io.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户登录信息
 *
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className LoginInfo
 * @author jj_D
 * @date: 2025/5/26 16:06
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    /**
     * 访问令牌
     */
    private String accessToken;
}
