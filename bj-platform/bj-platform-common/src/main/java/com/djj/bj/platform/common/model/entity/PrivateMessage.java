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
 * 私聊消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.domain.model.entity
 * @className PrivateMessage
 * @date 2025/7/13 19:33
 */
@Setter
@Getter
@TableName(value = "bj_private_message")
public class PrivateMessage extends Model<PrivateMessage> {

    @Serial
    private static final long serialVersionUID = 2570263814711189343L;

    /**
     * 私聊消息id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 发送者id
     */
    @TableField(value = "send_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sendId;

    /**
     * 接收者id
     */
    @TableField(value = "recv_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recvId;

    /**
     * 消息内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 消息类型 0:文字 1:图片 2:文件 3:语音 10:撤回消息
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 状态 0:未读 1:已读
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 发送时间
     */
    @TableField(value = "send_time")
    private Date sendTime;

    @Override
    public Serializable pkVal()
    {
        return this.id;
    }

}
