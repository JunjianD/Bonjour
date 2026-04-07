package com.djj.bj.ai.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP状态码枚举
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.common.enums
 * @enumName HttpCode
 * @date 2026/3/4 23:06
 */
@Getter
@AllArgsConstructor
public enum HttpCode {
    SUCCESS(200, "成功"),
    PROGRAM_ERROR(500, "系统繁忙，请稍后再试"),
    PARAMS_ERROR(402, "参数错误");

    private final Integer code;

    private final String msg;
}
