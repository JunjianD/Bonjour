package com.djj.bj.platform.message.application.service;

import com.djj.bj.platform.common.model.dto.GroupMessageDTO;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;

import java.util.List;

/**
 * 群聊消息服务
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service
 * @interfaceName GroupMessageService
 * @date 2025/8/7 10:39
 */
public interface GroupMessageService {
    /**
     * 发送群聊消息
     *
     * @param dto 群聊消息传输对象
     * @return 消息ID
     */
    Long sendMessage(GroupMessageDTO dto);


    /**
     * 保存群聊消息
     *
     * @param groupMessageTxEvent 群聊消息事务事件
     * @return 是否保存成功
     */
    boolean saveGroupMessageTxEvent(GroupMessageTxEvent groupMessageTxEvent);

    /**
     * 检测某条消息是否存在
     *
     * @param messageId 消息ID
     * @return 是否存在
     */
    boolean checkExists(Long messageId);

    /**
     * 异步拉取群聊未读消息，通过WebSocket异步推送
     */
    void pullUnreadMessage();

    /**
     * 拉取消息，只能拉取最近1个月的消息，一次拉取100条
     *
     * @param minId 最小消息ID
     * @return 群聊消息列表
     */
    List<GroupMessageVO> loadMessage(Long minId);

    /**
     * 拉取历史聊天记录
     *
     * @param groupId 群聊ID
     * @param page    页码
     * @param size    每页大小
     * @return 群聊消息列表
     */
    List<GroupMessageVO> findHistoryMessage(Long groupId, Long page, Long size);

    /**
     * 消息已读，同步其他终端，清空未读数量
     *
     * @param groupId 群聊ID
     */
    void readedMessage(Long groupId);

    /**
     * 撤回消息
     *
     * @param id 消息ID
     */
    void withdrawMessage(Long id);
}
