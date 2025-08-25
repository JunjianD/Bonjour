package com.djj.bj.platform.common.model.vo;

import com.alibaba.fastjson2.JSON;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 登录信息（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className LoginVO
 * @date 2025/7/22 20:51
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "用户登录信息（VO）")
public class LoginVO {
    @Schema(description = "每次请求都必须在header中携带accessToken")
    private String accessToken;

    @Schema(description = "accessToken过期时间，单位秒")
    private Integer accessTokenExpireTime;

    @Schema(description = "accessToken过期后，可以使用refreshToken刷新accessToken")
    private String refreshToken;

    @Schema(description = "refreshToken过期时间，单位秒")
    private Integer refreshTokenExpireTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
