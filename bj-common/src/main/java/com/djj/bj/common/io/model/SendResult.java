package com.djj.bj.common.io.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 发送结果
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className SendResult
 * @date 2025/7/7 11:12
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SendResult<T> extends BasicMessage {
    @Serial
    private static final long serialVersionUID = -5994363050413744187L;
    /**
     * 发送者
     */
    private UserInfo sender;

    /**
     * 接收者
     */
    private UserInfo receiver;

    /**
     * 发送状态
     */
    private Integer code;

    /**
     * 消息内容
     */
    private T data;
}
