package com.djj.bj.platform.message.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.FileType;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.vo.UploadImageVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.utils.FileUtils;
import com.djj.bj.platform.common.utils.ImageUtils;
import com.djj.bj.platform.message.application.minio.MinioService;
import com.djj.bj.platform.message.application.service.FileService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service.impl
 * @className FileServiceImpl
 * @date 2025/8/9 22:24
 */
@Service
public class FileServiceImpl implements FileService {
    private final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final MinioService minioService;

    public FileServiceImpl(MinioService minioService) {
        this.minioService = minioService;
    }

    @Value("${minio.public}")
    private String minIoServer;
    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.imagePath}")
    private String imagePath;
    @Value("${minio.filePath}")
    private String filePath;

    @PostConstruct
    public void init() {
        if (!minioService.bucketExists(bucketName)) {
            minioService.makeBucket(bucketName);
            minioService.setBucketPublic(bucketName);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        Long userId = SessionContext.getUserSession().getUserId();
        // 大小校验
        if (file.getSize() > Constants.MAX_FILE_SIZE) {
            long size = Constants.MAX_FILE_SIZE / 1024 / 1024;
            String infoStr = String.format("文件大小不能超过%dM", size);
            throw new BJException(HttpCode.PROGRAM_ERROR, infoStr);
        }
        // 上传
        String fileName = minioService.upload(bucketName, filePath, file);
        if (StringUtils.isEmpty(fileName)) {
            throw new BJException(HttpCode.PROGRAM_ERROR, "文件上传失败");
        }
        String url = getFileUrl(FileType.FILE, fileName);
        logger.info("FileServiceImpl.uploadFile | 文件上传成功，用户id:{}, url:{}", userId, url);
        return url;
    }

    @Override
    public UploadImageVO uploadImage(MultipartFile file) {
        try {
            Long userId = SessionContext.getUserSession().getUserId();
            // 大小校验
            if (file.getSize() > Constants.MAX_IMAGE_SIZE) {
                long size = Constants.MAX_IMAGE_SIZE / 1024 / 1024;
                String infoStr = String.format("图片大小不能超过%dM", size);
                throw new BJException(HttpCode.PROGRAM_ERROR, infoStr);
            }
            // 图片格式校验
            if (!FileUtils.isImage(file.getOriginalFilename())) {
                throw new BJException(HttpCode.PROGRAM_ERROR, "图片格式不合法");
            }
            // 上传原图
            UploadImageVO vo = new UploadImageVO();
            String fileName = minioService.upload(bucketName, imagePath, file);
            if (StringUtils.isEmpty(fileName)) {
                throw new BJException(HttpCode.PROGRAM_ERROR, "图片上传失败");
            }
            vo.setOriginUrl(getFileUrl(FileType.IMAGE, fileName));
            // 大于30K的文件需上传缩略图
            if (file.getSize() > PlatformConstants.IMAGE_COMPRESS_LIMIT) {
                byte[] imageByte = ImageUtils.compressForScale(file.getBytes(), PlatformConstants.IMAGE_COMPRESS_SIZE);
                fileName = minioService.upload(bucketName, imagePath, file.getOriginalFilename(), imageByte, file.getContentType());
                if (StringUtils.isEmpty(fileName)) {
                    throw new BJException(HttpCode.PROGRAM_ERROR, "图片上传失败");
                }
            }
            vo.setThumbUrl(getFileUrl(FileType.IMAGE, fileName));
            logger.info("FileServiceImpl.uploadImage | 图片上传成功，用户id:{}, url:{}", userId, vo.getOriginUrl());
            return vo;
        } catch (IOException e) {
            logger.error("FileServiceImpl.uploadImage | 上传图片失败，{}", e.getMessage(), e);
            throw new BJException(HttpCode.PROGRAM_ERROR, "图片上传失败");
        }
    }

    @Override
    public String getFileUrl(FileType fileTypeEnum, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(minIoServer)
                .append("/")
                .append(bucketName)
                .append(StrUtil.isEmpty(fileTypeEnum.getPath()) ? "" : fileTypeEnum.getPath())
                .append(fileName);
        return sb.toString();
    }
}
