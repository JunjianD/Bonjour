package com.djj.bj.platform.common.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则排序
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.enums
 * @enumName RuleEnum
 * @date 2025/7/15 15:33
 */
@Getter
@AllArgsConstructor
public enum RuleEnum {
    XSS(0, "XSS安全服务"),
    IP(1, "IP安全服务"),
    AUTH(10, "认证服务");

    private final Integer code;
    private final String message;
}
