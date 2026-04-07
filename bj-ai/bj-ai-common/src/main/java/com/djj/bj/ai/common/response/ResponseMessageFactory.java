package com.djj.bj.ai.common.response;

import com.djj.bj.ai.common.enums.HttpCode;

/**
 * 响应消息工厂类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.common.response
 * @className ResponseMessageFactory
 * @date 2026/3/4 23:10
 */
public class ResponseMessageFactory {
    public static <T> ResponseMessage<T> getSuccessResponseMessage() {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(HttpCode.SUCCESS.getMsg());
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getSuccessResponseMessage(T data){
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(HttpCode.SUCCESS.getMsg());
        responseMessage.setData(data);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getSuccessResponseMessage(T data, String message){
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(message);
        responseMessage.setData(data);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getSuccessResponseMessage(String message){
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(HttpCode.SUCCESS.getCode());
        responseMessage.setMessage(message);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getErrorResponseMessage(Integer code, String message){
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(code);
        responseMessage.setMessage(message);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getErrorResponseMessage(HttpCode httpCode, String message){
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(httpCode.getCode());
        responseMessage.setMessage(message);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> getErrorResponseMessage(HttpCode httpCode){
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setCode(httpCode.getCode());
        responseMessage.setMessage(httpCode.getMsg());
        return responseMessage;
    }
}
