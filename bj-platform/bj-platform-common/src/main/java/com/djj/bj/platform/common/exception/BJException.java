package com.djj.bj.platform.common.exception;

import com.djj.bj.platform.common.model.enums.HttpCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 自定义异常类，用于处理平台相关的异常情况
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.exception
 * @className BJException
 * @date 2025/7/14 16:52
 */
@Getter
public class BJException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3300320587121913181L;

    private final Integer code;
    private final String message;

    public BJException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BJException(HttpCode httpCode, String message) {
        this.code = httpCode.getCode();
        this.message = message;
    }

    public BJException(HttpCode httpCode){
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
    }

    public BJException(String message) {
        this.code = HttpCode.PROGRAM_ERROR.getCode();
        this.message = message;
    }
}
