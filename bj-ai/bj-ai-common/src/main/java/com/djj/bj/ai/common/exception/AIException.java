package com.djj.bj.ai.common.exception;

import com.djj.bj.ai.common.enums.HttpCode;
import lombok.Getter;

import java.io.Serial;

/**
 * AIException是一个自定义的运行时异常类，用于在AI相关的操作中抛出异常。
 * 它包含一个整数类型的错误代码和一个字符串类型的错误消息，以便更好地描述异常情况。
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.common.exception
 * @exceptionName AIException
 * @date 2026/3/4 23:09
 */
@Getter
public class AIException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5968809032438180653L;

    private final Integer code;
    private final String message;

    public AIException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public AIException(HttpCode httpCode, String message) {
        this.code = httpCode.getCode();
        this.message = message;
    }

    public AIException(HttpCode httpCode) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMsg();
    }

    public AIException(String message) {
        this.code = HttpCode.PROGRAM_ERROR.getCode();
        this.message = message;
    }
}
