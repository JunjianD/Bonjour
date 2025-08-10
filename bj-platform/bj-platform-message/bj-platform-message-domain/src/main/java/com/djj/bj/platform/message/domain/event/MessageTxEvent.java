package com.djj.bj.platform.message.domain.event;

import com.djj.bj.common.io.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 消息事务事件基类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.event
 * @className MessageTxEvent
 * @date 2025/8/6 20:08
 */
@NoArgsConstructor
@Getter
@Setter
public class MessageTxEvent extends BaseEvent {

    /**
     * 消息发送人id
     */
    private Long sendId;

    /**
     * 终端类型
     */
    private Integer terminal;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 消息类型：type_private: 私聊消息; type_group:群聊消息
     */
    private String messageType;

    public MessageTxEvent(Long id, Long sendId, Integer terminal, Date sendTime, String destination, String messageType) {
        super(id, destination);
        this.sendId = sendId;
        this.terminal = terminal;
        this.sendTime = sendTime;
        this.messageType = messageType;
    }

}
