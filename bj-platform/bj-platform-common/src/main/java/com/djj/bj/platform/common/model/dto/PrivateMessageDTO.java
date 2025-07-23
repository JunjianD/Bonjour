package com.djj.bj.platform.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 私信数据传输对象（Data Transfer Object, DTO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.dto
 * @className PrivateMessageDTO
 * @date 2025/7/22 20:36
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "私信数据传输对象(DTO)")
public class PrivateMessageDTO {
    @NotNull(message = "接收用户ID不可为空")
    @Schema(description = "接收用户ID")
    private Long receiverId;

    @Length(max = 1024, message = "内容长度不得大于1024")
    @NotEmpty(message = "发送内容不可为空")
    @Schema(description = "发送内容")
    private String content;

    @NotNull(message = "消息类型不可为空")
    @Schema(description = "消息类型")
    private Integer type;
}
