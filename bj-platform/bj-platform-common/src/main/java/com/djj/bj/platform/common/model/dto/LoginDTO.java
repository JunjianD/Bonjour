package com.djj.bj.platform.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 登录数据传输对象（Data Transfer Object, DTO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.dto
 * @className LoginDTO
 * @date 2025/7/22 19:58
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(
        description = "登录数据传输对象(DTO)",
        requiredProperties = {
                "terminal",
        }
)
public class LoginDTO {

    @Max(value = 1, message = "登录终端类型取值范围:0,1")
    @Min(value = 0, message = "登录终端类型取值范围:0,1")
    @NotNull(message = "登录终端类型不可为空")
    @Schema(
            description = "登录终端 0:web  1:app",
            example = "0"
    )
    private Integer terminal;

    @NotEmpty(message = "用户名不可为空")
    @Schema(description = "用户名")
    private String userName;

    @NotEmpty(message = "用户密码不可为空")
    @Schema(description = "用户密码")
    private String password;
}
