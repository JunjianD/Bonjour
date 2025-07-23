package com.djj.bj.platform.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class GroupMessageVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "群组ID")
    private Long groupId;

    @Schema(description = "发送者ID")
    private Long senderId;

    @Schema(description = "发送者昵称")
    private String senderNickName;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型")
    private Integer type;

    @Schema(description = "被@用户列表")
    private List<Long> atUserIds;

    @Schema(description = "消息状态")
    private Integer status;

    @Schema(description = "发送时间")
    private Date sendTime;
}
