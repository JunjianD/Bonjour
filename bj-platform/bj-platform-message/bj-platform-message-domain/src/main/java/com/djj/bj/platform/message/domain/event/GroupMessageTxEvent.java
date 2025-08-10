package com.djj.bj.platform.message.domain.event;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.GroupMessageDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 群聊消息事务事件
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.event
 * @className GroupMessageTxEvent
 * @date 2025/8/6 20:06
 */
@NoArgsConstructor
@Getter
@Setter
public class GroupMessageTxEvent extends MessageTxEvent {
    /**
     * 消息发送人昵称
     */
    private String sendNickName;

    /**
     * 接收消息的用户列表
     */
    private List<Long> recvIds;

    /**
     * 消息数据
     */
    private GroupMessageDTO groupMessageDTO;

    public GroupMessageTxEvent(Long id, Long sendId, String sendNickName, Integer terminal, Date sendTime, String destination, List<Long> recvIds, GroupMessageDTO groupMessageDTO) {
        super(id, sendId, terminal, sendTime, destination, PlatformConstants.TYPE_MESSAGE_GROUP);
        this.sendNickName = sendNickName;
        this.recvIds = recvIds;
        this.groupMessageDTO = groupMessageDTO;
    }

}
