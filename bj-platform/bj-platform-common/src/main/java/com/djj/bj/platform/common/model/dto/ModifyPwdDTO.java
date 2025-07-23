package com.djj.bj.platform.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 修改密码数据传输对象（Data Transfer Object, DTO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.dto
 * @className ModifyPwdDTO
 * @date 2025/7/22 20:49
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "修改密码数据传输对象(DTO)")
public class ModifyPwdDTO {
    @NotEmpty(message = "原密码不可为空")
    @Schema(description = "原密码")
    private String oldPassword;

    @NotEmpty(message = "新密码不可为空")
    @Schema(description = "新密码")
    private String newPassword;
}
