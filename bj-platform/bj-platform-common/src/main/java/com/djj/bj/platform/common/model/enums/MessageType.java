package com.djj.bj.platform.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.enums
 * @enumName MessageType
 * @date 2025/7/13 20:07
 */
@AllArgsConstructor
@Getter
public enum MessageType {
    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    FILE(2, "文件"),
    AUDIO(3, "音频"),
    VIDEO(4, "视频"),
    WITHDRAW(10, "撤回消息"),
    READED(11, "已读"),

    //Real Time Communication (RTC) message types
    RTC_CALL(101, "呼叫"),
    RTC_ACCEPT(102, "接受"),
    RTC_REJECT(103, "拒绝"),
    RTC_CANCEL(104, "取消呼叫"),
    RTC_FAILED(105, "呼叫失败"),
    RTC_HANDUP(106, "挂断"),
    RTC_CANDIDATE(107, "同步candidate");

    private final Integer code;
    private final String desc;
}
