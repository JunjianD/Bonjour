package com.djj.bj.platform.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 用户信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className UserVO
 * @date 2025/7/22 21:03
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "用户信息值对象（VO）")
public class UserVO {
    @NotNull(message = "用户ID不可为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotNull(message = "用户名不可为空")
    @Length(max = 64, message = "用户名不能大于64字符")
    @Schema(description = "用户名")
    private String userName;

    @NotNull(message = "用户昵称不可为空")
    @Length(max = 64, message = "用户昵称不能大于64字符")
    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户性别")
    private Integer sex;

    @Schema(description = "用户类型：1-普通用户，2-管理员")
    private Integer userType;

    @Length(max = 1024, message = "个性签名不能大于1024字符")
    @Schema(description = "个性签名")
    private String signature;

    @Schema(description = "用户头像URL")
    private String headImage;

    @Schema(description = "用户头像缩略图URL")
    private String headImageThumb;

    @Schema(description = "用户是否在线")
    private Boolean online;
}
