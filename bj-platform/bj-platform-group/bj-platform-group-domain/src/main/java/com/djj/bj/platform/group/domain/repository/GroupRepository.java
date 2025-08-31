package com.djj.bj.platform.group.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.djj.bj.platform.common.model.entity.Group;
import com.djj.bj.platform.common.model.vo.GroupVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 群组数据访问层接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.repository
 * @interfaceName GroupRepository
 * @date 2025/8/4 07:13
 */
public interface GroupRepository extends BaseMapper<Group> {
    @Select("select owner_id from bj_group where id = #{groupId}")
    Long getOwnerId(@Param("groupId") Long groupId);

    @Select("select g.id as id, g.name as name, g.owner_id as ownerId, g.head_image as headImage, " +
            "g.head_image_thumb as headImageThumb, g.notice as notice, gm.alias_name as aliasName, " +
            "gm.remark as remark from bj_group g left join bj_group_member gm on (g.id = gm.group_id) " +
            "where g.id = #{groupId} and gm.user_id = #{userId} and gm.quit = 0 ")
    GroupVO getGroupVOById(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Select("select g.id as id, g.name as name, g.owner_id as ownerId, g.head_image as headImage, " +
            "g.head_image_thumb as headImageThumb, g.notice as notice, gm.alias_name as aliasName, " +
            "gm.remark as remark from bj_group g left join bj_group_member gm on (g.id = gm.group_id) " +
            "where gm.user_id = #{userId} and g.deleted = 0 and gm.quit = 0")
    List<GroupVO> getGroupVOListByUserId(@Param("userId") Long userId);

    @Select("select name from bj_group where id = #{groupId}")
    String getGroupName(@Param("groupId") Long groupId);
}
