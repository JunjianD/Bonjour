package com.djj.bj.platform.common.exception;

import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.ConstraintViolationException;

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
    private final Logger logger = LoggerFactory.getLogger(BJExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = BJException.class)
    public ResponseMessage<String> handleBJException(BJException e) {
        return ResponseMessageFactory.getErrorResponseMessage(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class
    })
    public ResponseMessage<String> handleValidationException(Exception e) {
        return ResponseMessageFactory.getErrorResponseMessage(HttpCode.PARAMS_ERROR, getValidationMessage(e));
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseMessage<String> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseMessageFactory.getErrorResponseMessage(HttpCode.PROGRAM_ERROR);
    }

    private String getValidationMessage(Exception e) {
        FieldError fieldError = null;
        if (e instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
        } else if (e instanceof BindException bindException) {
            fieldError = bindException.getBindingResult().getFieldError();
        } else if (e instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException.getConstraintViolations().stream()
                    .findFirst()
                    .map(constraintViolation -> constraintViolation.getMessage())
                    .orElse(HttpCode.PARAMS_ERROR.getMessage());
        }
        return fieldError == null ? HttpCode.PARAMS_ERROR.getMessage() : fieldError.getDefaultMessage();
    }
}
