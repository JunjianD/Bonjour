package com.djj.bj.platform.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册数据传输对象（Data Transfer Object, DTO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.dto
 * @className RegisterDTO
 * @date 2025/7/22 20:32
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "用户注册数据传输对象(DTO)")
public class RegisterDTO {

    @Length(max = 64, message = "用户名不能大于64字符")
    @NotEmpty(message = "用户名不可为空")
    @Schema(description = "用户名")
    private String userName;

    @Length(min = 5, max = 20, message = "密码长度必须在5-20个字符之间")
    @NotEmpty(message = "用户密码不可为空")
    @Schema(description = "用户密码")
    private String password;

    @Length(max = 64, message = "昵称不能大于64字符")
    @NotEmpty(message = "用户昵称不可为空")
    @Schema(description = "用户昵称")
    private String nickName;
}
