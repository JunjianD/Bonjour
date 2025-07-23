package com.djj.bj.platform.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 群消息数据传输对象（Data Transfer Object, DTO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.dto
 * @className GroupMessageDTO
 * @date 2025/7/22 20:44
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "群消息数据传输对象(DTO)")
public class GroupMessageDTO {
    @NotNull(message = "群ID不可为空")
    @Schema(description = "群ID")
    private Long groupId;

    @Length(max = 1024, message = "发送内容长度不得大于1024")
    @NotEmpty(message = "发送内容不可为空")
    @Schema(description = "发送内容")
    private String content;

    @NotNull(message = "消息类型不可为空")
    @Schema(description = "消息类型")
    private Integer type;

    @Schema(description = "被@用户列表")
    @Size(max = 20, message = "单次最多只能@20个用户")
    private List<Long> atUserIds;
}
