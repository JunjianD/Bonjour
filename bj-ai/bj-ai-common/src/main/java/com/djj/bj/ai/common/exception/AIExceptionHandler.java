package com.djj.bj.ai.common.exception;

import com.djj.bj.ai.common.enums.HttpCode;
import com.djj.bj.ai.common.response.ResponseMessage;
import com.djj.bj.ai.common.response.ResponseMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.common.exception
 * @className AIExceptionHandler
 * @date 2026/3/4 23:09
 */
@ControllerAdvice(basePackages = "com.djj.bj.ai")
public class AIExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(AIExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(AIException.class)
    public ResponseMessage<String> handleBJException(AIException e) {
        logger.error("", e);
        return ResponseMessageFactory.getErrorResponseMessage(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseMessage<String> handleException(Exception e) {
        logger.error("", e);
        return ResponseMessageFactory.getErrorResponseMessage(HttpCode.PROGRAM_ERROR);
    }
}
