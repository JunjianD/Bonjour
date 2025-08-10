package com.djj.bj.platform.message.application.service;

import com.djj.bj.platform.common.model.enums.FileType;
import com.djj.bj.platform.common.model.vo.UploadImageVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service
 * @interfaceName FileService
 * @date 2025/8/9 22:20
 */
public interface FileService {
    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件的URL或路径
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传图片
     *
     * @param file 上传的图片文件
     * @return 图片的URL或路径
     */
    UploadImageVO uploadImage(MultipartFile file);

    /**
     * 生成文件URL
     *
     * @param fileTypeEnum 文件类型枚举
     * @param fileName     文件名
     * @return 文件的访问URL
     */
    String getFileUrl(FileType fileTypeEnum, String fileName);
}
