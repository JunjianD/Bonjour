package com.djj.bj.platform.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.enums
 * @enumName MessageStatus
 * @date 2025/7/13 20:05
 */
@AllArgsConstructor
@Getter
public enum MessageStatus {
    UNSEND(0, "未发送"),
    SENDED(1, "送达"),
    WITHDRAW(2, "撤回"),
    READED(3, "已读");

    private final Integer code;
    private final String desc;
}
