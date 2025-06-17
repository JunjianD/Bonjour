package com.djj.bj.common.io.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 所有终端类型枚举
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.enums
 * @enumName TerminalType
 * @date 2025/5/26 20:17
 */
@Getter
@AllArgsConstructor
public enum TerminalType {
    /**
     * PC端应用
     */
    PC(0, "PC端"),

    /**
     * 手机端
     */
    APP(1, "手机端"),

    /**
     * 平板端
     */
    TABLET(2, "平板端"),

    /**
     * Web端
     */
    WEB(3, "Web端");

    /**
     * 终端类型编码
     */
    private final Integer code;

    /**
     * 终端类型描述
     */
    private final String description;

    /**
     * 根据编码获取终端类型
     *
     * @param code 终端类型编码
     * @return TerminalType/null
     */
    @Nullable
    public static TerminalType fromCode(Integer code) {
        for (TerminalType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取所有终端类型的编码列表
     *
     * @return 所有终端类型的编码列表
     */
    public static List<Integer> getAllCodes() {
        return Arrays.stream(values())
                     .map(TerminalType::getCode)
                     .collect(Collectors.toList());
    }

}
