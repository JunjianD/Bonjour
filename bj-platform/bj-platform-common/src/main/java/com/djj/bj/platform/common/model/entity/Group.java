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
 * 群组信息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.entity
 * @className Group
 * @date 2025/7/13 17:42
 */
@Setter
@Getter
@TableName(value = "bj_group")
public class Group extends Model<Group> {

    @Serial
    private static final long serialVersionUID = 6214528161968671766L;

    /**
     * 群组id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 群组名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 群主id
     */
    @TableField(value = "owner_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerId;

    /**
     * 群组头像
     */
    @TableField(value = "head_image")
    private String headImage;

    /**
     * 群组头像缩略图
     */
    @TableField(value = "head_image_thumb")
    private String headImageThumb;

    /**
     * 群组公告
     */
    @TableField(value = "notice")
    private String notice;

    /**
     * 群组是否已经删除
     */
    @TableField(value = "deleted")
    private Boolean deleted;

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
