package com.djj.bj.common.io.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * 通用接收模型
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className ReceiveMessage
 * @date 2025/5/26 21:21
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReceiveMessage extends BasicMessage {
    @Serial
    private static final long serialVersionUID = 6903359647887948369L;

    /**
     * 命令类型
     */
    private Integer systemInfo;

    /**
     * 发送消息的用户信息
     */
    private UserInfo sender;

    /**
     * 接收消息的用户列表
     */
    List<UserInfo> receivers;

    /**
     * 是否需要回调发送结果
     */
    private Boolean sendResult;

    /**
     * 推送消息体
     */
    private Object content;
}
