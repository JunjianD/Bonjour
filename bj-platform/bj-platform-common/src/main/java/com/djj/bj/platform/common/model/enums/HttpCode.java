package com.djj.bj.platform.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * http结果状态码
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.enums
 * @enumName HttpCode
 * @date 2025/7/13 20:02
 */
@AllArgsConstructor
@Getter
public enum HttpCode {
    SUCCESS(200, "请求成功"),
    NO_LOGIN(400, "未登录"),
    INVALID_TOKEN(401, "token无效或已过期"),
    PROGRAM_ERROR(500, "系统繁忙，请稍后再试"),
    PASSWORD_ERROR(10001, "密码不正确"),
    USERNAME_ALREADY_REGISTER(10003, "用户名已被使用"),
    XSS_PARAM_ERROR(10004, "输入存在非法内容");

    private final int code;
    private final String message;
}
