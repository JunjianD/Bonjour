package com.djj.bj.platform.common.model.vo;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 群消息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className GroupMessageVO
 * @date 2025/7/22 21:23
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "群消息值对象（VO）")
public class GroupMessageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4342623521158057787L;

    @Schema(description = "消息ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "群组ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @Schema(description = "发送者ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sendId;

    @Schema(description = "发送者昵称")
    private String sendNickName;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型")
    private Integer type;

    @Schema(description = "被@用户列表")
    private List<Long> atUserIds;

    @Schema(description = "@用户列表")
    private String atUserIdsStr;

    @Schema(description = "消息状态")
    private Integer status;

    @Schema(description = "发送时间")
    private Date sendTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
