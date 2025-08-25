package com.djj.bj.platform.common.model.vo;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 群组信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className GroupVO
 * @date 2025/7/22 21:11
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "群组信息值对象（VO）")
public class GroupVO {
    @Schema(description = "群组ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Length(max = 32, message = "群组名称长度不能大于32")
    @NotEmpty(message = "群组名称不可为空")
    @Schema(description = "群组名称")
    private String name;

    @Schema(description = "群主id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerId;

    @Schema(description = "群头像")
    private String headImage;

    @Schema(description = "群头像缩略图")
    private String headImageThumb;

    @Length(max = 1024, message = "群组公告长度不能大于1024")
    @Schema(description = "群组公告")
    private String notice;

    @Length(max = 20, message = "用户显示的群昵称不能大于20")
    @Schema(description = "用户显示的群昵称")
    private String aliasName;

    @Length(max = 20, message = "群聊备注不能大于20")
    @Schema(description = "群聊备注")
    private String remark;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
