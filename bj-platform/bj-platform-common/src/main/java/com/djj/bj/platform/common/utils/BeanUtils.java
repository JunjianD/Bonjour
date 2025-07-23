package com.djj.bj.platform.common.utils;


import org.springframework.util.ReflectionUtils;

/**
 * 通用的Bean工具类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.utils
 * @className BeanUtils
 * @date 2025/7/22 21:52
 */
public class BeanUtils {
    private static void handleReflectionException(Exception e) {
        ReflectionUtils.handleReflectionException(e);
    }

    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        try {
            if (source == null) {
                return null;
            }
            Object targetInstance = targetClass.getConstructor().newInstance();
            copyProperties(source, targetInstance);
            return (T) targetInstance;
        } catch (Exception e) {
            handleReflectionException(e);
            return null;
        }
    }

    public static void copyProperties(Object source, Object target) {
        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }
}
