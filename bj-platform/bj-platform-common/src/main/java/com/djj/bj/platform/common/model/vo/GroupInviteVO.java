package com.djj.bj.platform.common.model.vo;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 群组邀请信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className GroupInviteVO
 * @date 2025/7/22 21:29
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "群组邀请信息值对象（VO）")
public class GroupInviteVO {

    @NotNull(message = "群组ID不可为空")
    @Schema(description = "群组ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @NotEmpty(message = "好友ID列表不可为空")
    @Schema(description = "好友ID列表")
    private List<Long> friendIds;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
