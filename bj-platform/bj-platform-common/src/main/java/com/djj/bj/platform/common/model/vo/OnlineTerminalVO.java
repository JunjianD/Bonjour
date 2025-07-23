package com.djj.bj.platform.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 在线终端信息值对象（Value Object, VO）
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className OnlineTerminalVO
 * @date 2025/7/22 21:17
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "在线终端信息值对象（VO）")
public class OnlineTerminalVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "终端ID")
    private List<Integer> terminalIds;
}
