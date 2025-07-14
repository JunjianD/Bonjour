package com.djj.bj.platform.common.response;

import com.djj.bj.platform.common.model.enums.HttpCode;

/**
 * 响应消息工厂类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.response
 * @className ResponseMessageFactory
 * @date 2025/7/14 17:18
 */
public class ResponseMessageFactory {
    public static <T> ResponseMessage<T> getSuccessResponseMessage() {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(HttpCode.SUCCESS.getMessage());
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getSuccessResponseMessage(T data) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(HttpCode.SUCCESS.getMessage());
        responseMessage.setData(data);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getSuccessResponseMessage(T data, String message) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(message);
        responseMessage.setData(data);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getSuccessResponseMessage(String message) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(message);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getErrorResponseMessage(Integer code, String message) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(code);
        responseMessage.setMessage(message);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getErrorResponseMessage(HttpCode httpCode, String message) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(httpCode.getCode());
        responseMessage.setMessage(message);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getErrorResponseMessage(HttpCode httpCode) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(httpCode.getCode());
        responseMessage.setMessage(httpCode.getMessage());
        return responseMessage;
    }
}
