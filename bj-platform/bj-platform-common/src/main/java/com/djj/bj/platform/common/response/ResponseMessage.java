package com.djj.bj.platform.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 响应消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.response
 * @className ResponseMessage
 * @date 2025/7/14 17:15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage<T> {

    private Integer code; // 响应码
    private String message; // 响应消息
    private T data; // 响应数据

    public ResponseMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
