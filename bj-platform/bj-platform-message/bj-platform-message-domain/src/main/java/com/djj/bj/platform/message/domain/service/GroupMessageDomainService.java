package com.djj.bj.platform.message.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.djj.bj.platform.common.model.entity.GroupMessage;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.message.domain.event.GroupMessageTxEvent;

import java.util.Date;
import java.util.List;

/**
 * 群聊消息领域服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.service
 * @interfaceName GroupMessageDomainService
 * @date 2025/8/5 16:51
 */
public interface GroupMessageDomainService extends IService<GroupMessage> {
    /**
     * 保存群聊消息
     *
     * @param groupMessageTxEvent
     * @return true: 保存成功, false: 保存失败
     */
    boolean saveGroupMessageTxEvent(GroupMessageTxEvent groupMessageTxEvent);

    /**
     * 查询群聊消息是否存在
     *
     * @param messageId 群聊消息ID
     * @return true: 存在, false: 不存在
     */
    boolean checkExists(Long messageId);

    /**
     * 拉取未读群聊消息列表
     *
     * @param groupId    群聊ID
     * @param sendTime   发送时间
     * @param sendId     发送者ID
     * @param status     消息状态
     * @param maxReadId  最大已读消息ID
     * @param limitCount 限制条数
     * @return 未读群聊消息列表
     */
    List<GroupMessageVO> getUnreadGroupMessageList(Long groupId, Date sendTime, Long sendId, Integer status, Long maxReadId, Integer limitCount);

    /**
     * 拉取全站群聊消息列表
     *
     * @param minId      最小消息ID
     * @param minDate    最小发送时间
     * @param ids        群聊ID列表
     * @param status     消息状态
     * @param limitCount 限制条数
     * @return 全站群聊消息列表
     */
    List<GroupMessageVO> loadGroupMessageList(Long minId, Date minDate, List<Long> ids, Integer status, Integer limitCount);

    /**
     * 拉取某个群的历史消息
     *
     * @param groupId  群聊ID
     * @param sendTime 发送时间
     * @param status   消息状态
     * @param stIdx    起始索引
     * @param size     每页大小
     * @return 群聊历史消息列表
     */
    List<GroupMessageVO> getHistoryMessage(Long groupId, Date sendTime, Integer status, long stIdx, long size);

    /**
     * 获取群聊的最大消息ID
     *
     * @param groupId 群聊ID
     * @return 最大消息ID
     */
    Long getMaxMessageId(Long groupId);

    /**
     * 查询指定的群聊消息
     *
     * @param messageId 群聊消息ID
     * @return 群聊消息视图对象
     */
    GroupMessageVO getGroupMessageById(Long messageId);

    /**
     * 更新群聊消息状态
     *
     * @param status    消息状态
     * @param messageId 群聊消息ID
     * @return 更新的行数
     */
    int updateStatus(Integer status, Long messageId);
}
