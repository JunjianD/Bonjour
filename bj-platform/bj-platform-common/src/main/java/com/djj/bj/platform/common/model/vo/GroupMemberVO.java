package com.djj.bj.platform.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 群成员信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className GroupMemberVO
 * @date 2025/7/22 21:27
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "群成员信息值对象（VO）")
public class GroupMemberVO {

    @Schema(description = "群成员ID")
    private Long userId;

    @Schema(description = "群内昵称")
    private String aliasName;

    @Schema(description = "用户头像")
    private String headImage;

    @Schema(description = "用户是否已经退出群聊")
    private Boolean quit;

    @Schema(description = "用户是否在线")
    private Boolean online;

    @Schema(description = "备注")
    private String remark;
}
