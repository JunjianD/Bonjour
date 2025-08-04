package com.djj.bj.platform.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 群成员
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.entity
 * @className GroupMember
 * @date 2025/7/13 19:02
 */
@Setter
@Getter
@TableName(value = "bj_group_member")
public class GroupMember extends Model<GroupMember> {

    @Serial
    private static final long serialVersionUID = -6467165159919009446L;

    /**
     * 群成员数据id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 群id
     */
    @TableField(value = "group_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 群成员昵称
     */
    @TableField(value = "alias_name")
    private String aliasName;

    /**
     * 群成员头像
     */
    @TableField(value = "head_image")
    private String headImage;

    /**
     * 群聊备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 是否已经离开群聊
     */
    @TableField(value = "quit")
    private Boolean quit;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
