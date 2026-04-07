package com.djj.bj.ai.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 响应消息类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.common.response
 * @className ResponseMessage
 * @date 2026/3/4 23:09
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;

    public ResponseMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
