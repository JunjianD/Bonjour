package com.djj.bj.platform.common.utils;

/**
 * 文件工具类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.utils
 * @className FileUtils
 * @date 2025/7/23 08:36
 */
public class FileUtils {
    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 判断文件是否为图片
     *
     * @param fileName 文件名
     * @return 如果是图片返回true，否则返回false
     */
    public static boolean isImage(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        for (String imageExtension : imageExtensions) {
            if (extension.equals(imageExtension)) {
                return true;
            }
        }
        return false;
    }
}
