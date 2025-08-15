package com.djj.bj.platform.common.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 群成员简易信息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className GroupMemberSimpleVO
 * @date 2025/8/4 21:40
 */
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "群成员简易信息（VO）")
public class GroupMemberSimpleVO {

    @Schema(description = "群内昵称")
    private String aliasName;

    @Schema(description = "用户是否已经退出群聊")
    private Boolean quit;

    @Schema(description = "群组ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @Schema(description = "创建时间")
    private Date createTime;

    public GroupMemberSimpleVO(String aliasName, Boolean quit) {
        this.aliasName = aliasName;
        this.quit = quit;
    }
}
