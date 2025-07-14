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
 * 群消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.entity
 * @className GroupMessage
 * @date 2025/7/13 19:39
 */
@Setter
@Getter
@TableName(value = "bj_group_message")
public class GroupMessage extends Model<GroupMessage> {

    @Serial
    private static final long serialVersionUID = -1797078872991306722L;

    /**
     * id
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
     * 发送用户id
     */
    @TableField(value = "send_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sendId;

    /**
     * 发送用户昵称
     */
    @TableField(value = "send_nick_name")
    private String sendNickName;

    /**
     * @ 用户列表
     */
    @TableField(value = "at_user_ids")
    private String atUserIds;

    /**
     * 发送内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 消息类型 0:文字 1:图片 2:文件 3:语音 10:撤回消息
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 发送时间
     */
    @TableField(value = "send_time")
    private Date sendTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
