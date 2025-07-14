package com.djj.bj.platform.common.exception;

import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常捕获
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.exception
 * @className BJExceptionHandler
 * @date 2025/7/14 17:13
 */
@ControllerAdvice
public class BJExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = BJException.class)
    public ResponseMessage<String> handleBJException(BJException e) {
        return ResponseMessageFactory.getErrorResponseMessage(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseMessage<String> handleException(Exception e) {
        return ResponseMessageFactory.getErrorResponseMessage(HttpCode.PROGRAM_ERROR);
    }
}
