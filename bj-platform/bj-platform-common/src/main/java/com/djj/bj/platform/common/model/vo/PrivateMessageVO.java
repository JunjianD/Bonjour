package com.djj.bj.platform.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 私信信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className PrivateMessageVO
 * @date 2025/7/22 21:19
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "私信信息值对象（VO）")
public class PrivateMessageVO {

    @Schema(description = "私信ID")
    private Long id;

    @Schema(description = "发送者ID")
    private Long sendId;

    @Schema(description = "接收者ID")
    private Long recvId;

    @Schema(description = "私信内容")
    private String content;

    @Schema(description = "私信类型")
    private Integer type;

    @Schema(description = "私信状态")
    private Integer status;

    @Schema(description = "发送时间")
    private Date sendTime;
}
