package com.djj.bj.common.io.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送消息的状态
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.enums
 * @enumName RESPONSETYPE
 * @date 2025/5/26 20:04
 */
@Getter
@AllArgsConstructor
public enum ResponseType {
    SUCCESS(0, "发送成功"),
    OFFLINE(1, "用户不在线"),
    MISSING_CHANNEL(2, "未搜寻到频道"),
    UNKNOWN_ERROR(-1, "未知错误");

    /**
     * 响应类型编码
     */
    private final Integer code;

    /**
     * 响应类型描述
     */
    private final String description;
}
