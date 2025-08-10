package com.djj.bj.platform.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 图片上传VO
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.model.vo
 * @className UploadImageVO
 * @date 2025/8/9 22:22
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "图片上传VO")
public class UploadImageVO {
    @Schema(description = "原图")
    private String originUrl;

    @Schema(description = "缩略图")
    private String thumbUrl;
}
