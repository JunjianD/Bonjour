package com.djj.bj.platform.message.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.djj.bj.platform.common.model.entity.PrivateMessage;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 私聊消息数仓
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.repository
 * @interfaceName PrivateMessageRepository
 * @date 2025/8/5 11:49
 */
public interface PrivateMessageRepository extends BaseMapper<PrivateMessage> {
    @Select("select 1 from bj_private_message where id = #{messageId} limit 1")
    Integer checkExists(@Param("messageId") Long messageId);

    @Select({"<script> " +
            "select id as id, send_id as sendId, recv_id as recvId, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_private_message where recv_id = #{userId} and status = 0 and send_id in   " +
            "<foreach collection='friendIds' item='friendId' index='index' separator=',' open='(' close=')'> " +
            " #{friendId} " +
            " </foreach> " +
            "</script>"})
    List<PrivateMessageVO> getPrivateMessageVOList(@Param("userId") Long userId, @Param("friendIds") List<Long> friendIds);

    @Select({"<script> " +
            "select id as id, send_id as sendId, recv_id as recvId, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_private_message where id  <![CDATA[ > ]]> #{minId} and send_time  <![CDATA[ >= ]]> #{minDate} and status  <![CDATA[ <> ]]> 2 and ( " +
            " ( " +
            "send_id = #{userId} and recv_id in " +
            "<foreach collection='friendIds' item='friendId' index='index' separator=',' open='(' close=')'> " +
            " #{friendId} " +
            " </foreach> " +
            " ) " +
            " or " +
            " (" +
            "recv_id = #{userId} and send_id in " +
            "<foreach collection='friendIds' item='friendId' separator=',' open='(' close=')'> " +
            " #{friendId} " +
            " </foreach> " +
            ") " +
            " ) order by id asc limit #{limitCount} " +
            "</script>"})
    List<PrivateMessageVO> loadMessage(@Param("userId") Long userId, @Param("minId") Long minId, @Param("minDate") Date minDate, @Param("friendIds") List<Long> friendIds, @Param("limitCount") int limitCount);

    @Update({"<script> " +
            "update bj_private_message set status = #{status} where id in " +
            " <foreach collection='ids' item='id' index='index' separator=',' open='(' close=')'>  " +
            " #{id} " +
            " </foreach> " +
            "</script>"})
    int batchUpdatePrivateMessageStatus(@Param("status") Integer status, @Param("ids") List<Long> ids);

    @Select({"<script> " +
            "select id as id, send_id as sendId, recv_id as recvId, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_private_message where " +
            " ((send_id = #{userId} and recv_id = #{friendId}) or (send_id = #{friendId} and recv_id = #{userId})) " +
            "and status  <![CDATA[ <> ]]> 2 order by id desc limit #{stIdx}, #{size} " +
            "</script>"})
    List<PrivateMessageVO> loadMessageByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("stIdx") long stIdx, @Param("size") long size);

    @Update("update bj_private_message set status = 3 where send_id = #{sendId} and recv_id = #{recvId} and status = 1 ")
    int readedMessage(@Param("sendId") Long sendId, @Param("recvId") Long recvId);

    @Update("update bj_private_message set status = #{status} where id = #{messageId}")
    int updateMessageStatusById(@Param("status") Integer status, @Param("messageId") Long messageId);

    @Select({"<script> " +
            "select id as id, send_id as sendId, recv_id as recvId, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_private_message where id = #{messageId} " +
            "</script>"})
    PrivateMessageVO getPrivateMessageById(@Param("messageId") Long messageId);
}
