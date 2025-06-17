package com.djj.bj.common.io.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 通用发送模型，用于服务向客户端发送响应结果和具体消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className SendMessage
 * @date 2025/5/26 20:53
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessage<T> {
    /**
     * SystemInfo的值id
     */
    private Integer systeminfo;

    /**
     * 消息内容
     */
    private T content;
}
