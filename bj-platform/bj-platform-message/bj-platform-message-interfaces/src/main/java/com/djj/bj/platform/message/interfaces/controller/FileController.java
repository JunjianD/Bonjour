package com.djj.bj.platform.message.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.vo.UploadImageVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.message.application.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.interfaces.controller
 * @className FileController
 * @date 2025/8/9 22:39
 */
@RestController
@Tag(name = "文件上传", description = "提供文件上传功能，包括图片和其他文件的上传")
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片", description = "上传图片,上传后返回原图和缩略图的url", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<UploadImageVO> uploadImage(@RequestPart(name = "file") MultipartFile file) {
        return ResponseMessageFactory.getSuccessResponseMessage(fileService.uploadImage(file));
    }

    @PostMapping("/file/upload")
    @Operation(summary = "上传文件", description = "上传文件，上传后返回文件url", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))

    })
    public ResponseMessage<String> uploadFile(@RequestPart(name = "file") MultipartFile file) {
        return ResponseMessageFactory.getSuccessResponseMessage(fileService.uploadFile(file), HttpCode.SUCCESS.getMessage());
    }
}
