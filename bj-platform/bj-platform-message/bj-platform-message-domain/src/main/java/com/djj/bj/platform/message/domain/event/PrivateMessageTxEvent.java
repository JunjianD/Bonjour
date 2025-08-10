package com.djj.bj.platform.message.domain.event;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.PrivateMessageDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 私聊消息事务事件
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.event
 * @className PrivateMessageTxEvent
 * @date 2025/8/5 11:43
 */
@NoArgsConstructor
@Getter
@Setter
public class PrivateMessageTxEvent extends MessageTxEvent {
    /**
     * 消息数据
     */
    private PrivateMessageDTO privateMessageDTO;

    public PrivateMessageTxEvent(Long id, Long sendId, Integer terminal, String destination, Date sendTime, PrivateMessageDTO privateMessageDTO) {
        super(id, sendId, terminal, sendTime, destination, PlatformConstants.TYPE_MESSAGE_PRIVATE);
        this.privateMessageDTO = privateMessageDTO;
    }
}
