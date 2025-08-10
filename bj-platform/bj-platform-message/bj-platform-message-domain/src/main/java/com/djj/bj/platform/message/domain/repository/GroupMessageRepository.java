package com.djj.bj.platform.message.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.djj.bj.platform.common.model.entity.GroupMessage;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 群消息数仓接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.domain.repository
 * @interfaceName GroupMessageRepository
 * @date 2025/8/5 16:50
 */
public interface GroupMessageRepository extends BaseMapper<GroupMessage> {
    @Select("select 1 from bj_group_message where id = #{messageId} limit 1")
    Integer checkExists(@Param("messageId") Long messageId);

    @Select({"<script> " +
            "select id as id, group_id as groupId, send_id as sendId, send_nick_name as sendNickName, " +
            "at_user_ids as atUserIdsStr, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_group_message where group_id = #{groupId} and send_time <![CDATA[ > ]]> #{sendTime} " +
            "and send_id  <![CDATA[ <> ]]> #{sendId} and status  <![CDATA[ <> ]]> #{status} " +
            " <if test=\"maxReadId != null and maxReadId != 0\"> " +
            " and id <![CDATA[ > ]]> #{maxReadId} " +
            "</if> " +
            "limit #{limitCount}" +
            "</script>"})
    List<GroupMessageVO> getUnreadGroupMessageList(@Param("groupId") Long groupId, @Param("sendTime") Date sendTime,
                                                   @Param("sendId") Long sendId, @Param("status") Integer status,
                                                   @Param("maxReadId") Long maxReadId, @Param("limitCount") Integer limitCount);

    @Select({"<script> " +
            "select id as id, group_id as groupId, send_id as sendId, send_nick_name as sendNickName, " +
            "at_user_ids as atUserIdsStr, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_group_message where id <![CDATA[ > ]]> #{minId} and send_time <![CDATA[ > ]]> #{minDate}  and group_id in " +
            "<foreach collection='ids' item='id' index='index' separator=',' open='(' close=')'> " +
            " #{id} " +
            " </foreach> " +
            " and status  <![CDATA[ <> ]]> #{status} " +
            " order by id asc limit #{limitCount} " +
            "</script>"})
    List<GroupMessageVO> loadGroupMessageList(@Param("minId") Long minId, @Param("minDate") Date minDate, @Param("ids") List<Long> ids,
                                              @Param("status") Integer status, @Param("limitCount") Integer limitCount);

    @Select({"<script> " +
            "select id as id, group_id as groupId, send_id as sendId, send_nick_name as sendNickName, " +
            "at_user_ids as atUserIdsStr, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_group_message where group_id = #{groupId} and send_time <![CDATA[ > ]]> #{sendTime}   " +
            " and status  <![CDATA[ <> ]]> #{status} order by id desc limit #{stIdx}, #{size}" +
            "</script>"})
    List<GroupMessageVO> getHistoryMessage(@Param("groupId") Long groupId, @Param("sendTime") Date sendTime,
                                           @Param("status") Integer status, @Param("stIdx") long stIdx, @Param("size") long size);

    @Select("select id from bj_group_message where group_id = #{groupId} order by id desc limit 1")
    Long getMaxMessageId(@Param("groupId") Long groupId);

    @Select({"<script> " +
            "select id as id, group_id as groupId, send_id as sendId, send_nick_name as sendNickName, " +
            "at_user_ids as atUserIdsStr, content as content, type as type, status as status, send_time as sendTime " +
            "from bj_group_message where id = #{messageId} " +
            "</script>"})
    GroupMessageVO getGroupMessageById(@Param("messageId") Long messageId);

    @Update("update bj_group_message set status = #{status} where id = #{messageId} ")
    int updateStatus(@Param("status") Integer status, @Param("messageId") Long messageId);
}
