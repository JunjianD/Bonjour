package com.djj.bj.platform.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型枚举
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.enums
 * @enumName FileType
 * @date 2025/7/13 19:59
 */
@AllArgsConstructor
@Getter
public enum FileType {
    FILE(0, "文件"),
    IMAGE(1, "图片"),
    VIDEO(2, "视频"),
    AUDIO(3, "语音");

    private final Integer code;
    private final String desc;
}
