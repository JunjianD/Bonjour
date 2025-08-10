package com.djj.bj.platform.message.application.minio;

import org.springframework.web.multipart.MultipartFile;

/**
 * Minio文件上传服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.minio
 * @interfaceName MinioService
 * @date 2025/8/9 21:52
 */
public interface MinioService {
    /**
     * 查看存储bucket是否存在
     *
     * @param bucketName bucket名称
     * @return true/false
     */
    Boolean bucketExists(String bucketName);

    /**
     * 创建存储bucket
     *
     * @param bucketName bucket名称
     */
    void makeBucket(String bucketName);

    /**
     * 设置bucket权限为public
     *
     * @param bucketName bucket名称
     */
    void setBucketPublic(String bucketName);

    /**
     * 文件上传
     *
     * @param bucketName bucket名称
     * @param path       路径
     * @param file       文件
     * @return Boolean
     */
    String upload(String bucketName, String path, MultipartFile file);

    /**
     * 文件上传
     *
     * @param bucketName  bucket名称
     * @param path        路径
     * @param name        文件名
     * @param fileByte    文件内容
     * @param contentType contentType
     * @return objectName
     */
    String upload(String bucketName, String path, String name, byte[] fileByte, String contentType);

    /**
     * 删除
     *
     * @param bucketName bucket名称
     * @param path       路径
     * @param fileName   文件名
     * @return true/false
     */
    boolean remove(String bucketName, String path, String fileName);
}
