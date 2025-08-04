package com.djj.bj.platform.friend.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.djj.bj.platform.common.model.entity.Friend;
import com.djj.bj.platform.common.model.vo.FriendVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 好友数据仓库接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.domain.repository
 * @interfaceName FriendRepository
 * @date 2025/8/1 15:04
 */
public interface FriendRepository extends BaseMapper<Friend> {
    /**
     * 根据用户ID获取好友列表
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    @Select("select id as id, friend_nick_name as nickName, friend_head_image as headImage from bj_friend where user_id = #{userId}")
    List<FriendVO> getFriendVOList(@Param("userId") Long userId);

    /**
     * 根据好友ID和用户ID获取好友信息
     *
     * @param friendId 好友ID
     * @param userId   用户ID
     * @return 好友信息
     */
    @Select("select id as id, friend_nick_name as nickName, friend_head_image as headImage from bj_friend where friend_id = #{friendId} and user_id = #{userId}")
    FriendVO getFriendVO(@Param("friendId") Long friendId, @Param("userId") Long userId);

    /**
     * 检查是否是好友关系
     *
     * @param friendId 好友ID
     * @param userId   用户ID
     * @return 好友关系存在返回1，否则返回null
     */
    @Select("select 1 from bj_friend where friend_id = #{friendId} and user_id = #{userId} limit 1 ")
    Integer checkFriend(@Param("friendId") Long friendId, @Param("userId") Long userId);

    /**
     * 更新好友信息
     *
     * @param headImage 好友头像
     * @param nickName  好友昵称
     * @param friendId  好友ID
     * @param userId    用户ID
     * @return 更新成功返回1，否则返回0
     */
    @Update("update bj_friend set friend_head_image = #{headImage}, friend_nick_name = #{nickName} where friend_id = #{friendId} and user_id = #{userId} ")
    int updateFriend(@Param("headImage") String headImage, @Param("nickName") String nickName, @Param("friendId") Long friendId, @Param("userId") Long userId);

    /**
     * 删除好友关系
     *
     * @param friendId 好友ID
     * @param userId   用户ID
     * @return 删除成功返回1，否则返回0
     */
    @Delete("delete from bj_friend where friend_id = #{friendId} and user_id = #{userId} ")
    int deleteFriend(@Param("friendId") Long friendId, @Param("userId") Long userId);

    /**
     * 查询用户的所有好友ID列表
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    @Select("select friend_id from im_friend where user_id = #{userId}")
    List<Long> getFriendIdList(@Param("userId") Long userId);

    @Select("select id as id, user_id as userId, friend_id as friendId, friend_nick_name as friendNickName, friend_head_image as friendHeadImage, created_time as createdTime " +
            "from im_friend where user_id = #{userId} ")
    List<Friend> getFriendByUserId(@Param("userId") Long userId);

    @Update("<script>" +
            "update im_friend set " +
            "<set>" +
            "<if test = \"headImage != null and headImage != ''\">" +
            "friend_head_image = #{headImage} " +
            "</if>" +
            "<if test = \"nickName != null and nickName != ''\">" +
            "friend_nick_name = #{nickName} " +
            "</if>" +
            "</set>" +
            " where friend_id = #{friendId}" +
            "</script>")
    int updateFriendByFriendId(@Param("headImage") String headImage, @Param("nickName") String nickName, @Param("friendId") Long friendId);

}
