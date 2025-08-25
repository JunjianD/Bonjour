package com.djj.bj.platform.common.model.vo;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 好友信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className FriendVO
 * @date 2025/7/22 21:08
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "好友信息值对象（VO）")
public class FriendVO {
    @NotNull(message = "好友ID不可为空")
    @Schema(description = "好友ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @NotNull(message = "好友昵称不可为空")
    @Schema(description = "好友昵称")
    private String nickName;

    @Schema(description = "好友头像")
    private String headImage;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
