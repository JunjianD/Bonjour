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
 * 用户信息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.entity
 * @className User
 * @date 2025/7/13 09:35
 */
@Setter
@Getter
@TableName(value = "bj_user")
public class User extends Model<User> {

    @Serial
    private static final long serialVersionUID = 1205488643089491562L;

    /**
     * id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 登录用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 性别
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 头像
     */
    @TableField(value = "head_image")
    private String headImage;

    /**
     * 头像缩略图
     */
    @TableField(value = "head_image_thumb")
    private String headImageThumb;

    /**
     * 用户类型 1:普通用户 2: 超管
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 个性签名
     */
    @TableField(value = "signature")
    private String signature;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 最后登录时间
     */
    @TableField(value = "last_login_time")
    private Date lastLoginTime;

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
