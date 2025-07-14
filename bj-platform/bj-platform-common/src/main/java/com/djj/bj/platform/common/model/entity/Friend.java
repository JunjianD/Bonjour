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
 * 好友信息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.entity
 * @className Friend
 * @date 2025/7/13 17:06
 */
@Setter
@Getter
@TableName(value = "bj_friend")
public class Friend extends Model<Friend> {

    @Serial
    private static final long serialVersionUID = -1145067392543907877L;

    /**
     * 好友关系id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    
    /**
     * 好友id
     */
    @TableField(value = "friend_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;
    
    /**
     * 好友昵称
     */
    @TableField(value = "friend_nick_name")
    private String friendNickName;
    
    /**
     * 好友头像
     */
    @TableField(value = "friend_head_image")
    private String friendHeadImage;
    
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
