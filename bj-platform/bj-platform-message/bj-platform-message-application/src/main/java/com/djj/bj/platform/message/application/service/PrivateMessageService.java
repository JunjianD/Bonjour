package com.djj.bj.platform.message.application.service;

import com.djj.bj.platform.common.model.dto.PrivateMessageDTO;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;

import java.util.List;

/**
 * 私聊消息服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service
 * @interfaceName PrivateMessageService
 * @date 2025/8/5 11:37
 */
public interface PrivateMessageService {
    /**
     * 发送私聊消息
     *
     * @param dto 私聊消息
     * @return 消息id
     */
    Long sendMessage(PrivateMessageDTO dto);

    /**
     * 保存私聊消息
     *
     * @param privateMessageTxEvent 私聊消息事件
     * @return 是否保存成功
     */
    boolean savePrivateMessageTxEvent(PrivateMessageTxEvent privateMessageTxEvent);

    /**
     * 检测数据
     *
     * @param messageId 消息id
     * @return 是否存在
     */
    boolean checkExists(Long messageId);

    /**
     * 异步拉取单聊未读消息
     */
    void pullUnreadMessage();

    /**
     * 拉取最近1个月的消息，一次拉取100条
     *
     * @param minId 最小消息ID
     * @return 最近的私聊消息列表
     */
    List<PrivateMessageVO> loadMessage(Long minId);

    /**
     * 拉取历史聊天记录
     *
     * @param friendId 好友ID
     * @param page     页码
     * @param size     每页大小
     * @return 历史聊天记录列表
     */
    List<PrivateMessageVO> getHistoryMessage(Long friendId, Long page, Long size);

    /**
     * 将整个会话的消息都置为已读状态
     *
     * @param friendId 好友ID
     */
    void readedMessage(Long friendId);

    /**
     * 撤回消息
     *
     * @param id 消息ID
     */
    void withdrawMessage(Long id);
}
