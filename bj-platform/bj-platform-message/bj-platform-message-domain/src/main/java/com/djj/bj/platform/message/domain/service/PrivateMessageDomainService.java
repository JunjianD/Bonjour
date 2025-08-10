package com.djj.bj.platform.message.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.djj.bj.platform.common.model.entity.PrivateMessage;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.message.domain.event.PrivateMessageTxEvent;

import java.util.Date;
import java.util.List;

/**
 * 私聊消息领域服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.service
 * @interfaceName PrivateMessageDomainService
 * @date 2025/8/5 11:51
 */
public interface PrivateMessageDomainService extends IService<PrivateMessage> {
    /**
     * 保存私聊消息
     *
     * @param privateMessageTxEvent 私聊消息事件
     * @return 是否保存成功
     */
    boolean savePrivateMessageTxEvent(PrivateMessageTxEvent privateMessageTxEvent);

    /**
     * 检测某条消息是否存在
     *
     * @param messageId 消息id
     * @return 是否存在
     */
    boolean checkExists(Long messageId);

    /**
     * 获取所有未读的私聊消息
     *
     * @param userId    用户ID
     * @param friendIds 好友ID列表
     * @return 未读的私聊消息列表
     */
    List<PrivateMessage> getAllUnreadPrivateMessage(Long userId, List<Long> friendIds);

    /**
     * 获取所有未读的私聊消息
     *
     * @param userId    用户ID
     * @param friendIds 好友ID列表
     * @return 未读的私聊消息视图对象列表
     */
    List<PrivateMessageVO> getPrivateMessageVOList(Long userId, List<Long> friendIds);

    /**
     * 拉取消息，只能拉取最近1个月的消息，一次拉取100条
     *
     * @param userId     用户ID
     * @param minId      最小消息ID
     * @param minDate    最小日期
     * @param friendIds  好友ID列表
     * @param limitCount 限制条数
     * @return 最近的私聊消息视图对象列表
     */
    List<PrivateMessageVO> loadMessage(Long userId, Long minId, Date minDate, List<Long> friendIds, int limitCount);

    /**
     * 批量更新私聊消息状态
     *
     * @param status 新状态
     * @param ids    消息ID列表
     * @return 更新的记录数
     */
    int batchUpdatePrivateMessageStatus(Integer status, List<Long> ids);

    /**
     * 拉取指定用户与好友的历史消息
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @param stIdx    起始索引
     * @param size     每页大小
     * @return 指定用户与好友的历史消息列表
     */
    List<PrivateMessageVO> loadMessageByUserIdAndFriendId(Long userId, Long friendId, long stIdx, long size);

    /**
     * 将指定用户与好友的消息更新为已读状态
     *
     * @param sendId 发送者ID
     * @param recvId 接收者ID
     * @return 更新的记录数
     */
    int readedMessage(Long sendId, Long recvId);

    /**
     * 根据id修改状态
     *
     * @param status    新状态
     * @param messageId 消息ID
     * @return 更新的记录数
     */
    int updateMessageStatusById(Integer status, Long messageId);

    /**
     * 获取私聊消息详情
     *
     * @param messageId 消息ID
     * @return 私聊消息视图对象
     */
    PrivateMessageVO getPrivateMessageById(Long messageId);

}
