package com.djj.bj.common.io.model;

import com.djj.bj.common.io.enums.TerminalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 私发消息模型
 *
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className PrivateChat
 * @author jj_D
 * @date 2025/5/26 16:07
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PrivateChat<T> {
    /**
     * 发送者
     */
    private UserInfo sender;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者终端类型
     *
     * @default 所有终端
     */
    private List<Integer> receiverTerminals = TerminalType.getAllCodes();

    /**
     * 是否发送给自己的其他终端
     * @default true
     */
    private Boolean sendToSelfOtherTerminals = true;

    /**
     * 是否返回发送结果
     * @default true
     */
    private Boolean returnResult = true;

    /**
     * 消息内容
     */
    private T content;
}
