package com.djj.bj.common.cache.distribute.conversion;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;

import java.util.Collection;

/**
 * 简单类型转换工具类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.distribute.conversion
 * @className TypeConversion
 * @date 2025/6/3 20:41
 */
public class TypeConversion {
    public static <T> boolean isCollectionType(T t) {
        return t instanceof Collection;
    }

    public static <T> boolean isString(T t) {
        return t instanceof String;
    }

    public static <T> boolean isSimpleString(T t){
        if (!isString(t)) {
            return false;
        }
        return !JSONUtil.isTypeJSON(t.toString());
    }

    public static <T> boolean isByte(T t) {
        return t instanceof Byte;
    }

    public static <T> boolean isShort(T t) {
        return t instanceof Short;
    }

    public static <T> boolean isInt(T t) {
        return t instanceof Integer;
    }

    public static <T> boolean isLong(T t) {
        return t instanceof Long;
    }

    public static <T> boolean isChar(T t) {
        return t instanceof Character;
    }

    public static <T> boolean isFloat(T t) {
        return t instanceof Float;
    }

    public static <T> boolean isDouble(T t) {
        return t instanceof Double;
    }

    public static <T> boolean isBoolean(T t) {
        return t instanceof Boolean;
    }

    public static <T> boolean isSimpleType(T t) {
        return isSimpleString(t) || isInt(t) || isLong(t) || isDouble(t) || isFloat(t) || isChar(t) || isBoolean(t) || isShort(t) || isByte(t);
    }

    public static <T> Class<?> getClassType(T t) {
        return t.getClass();
    }

    public static <T> T convertor(String str, Class<T> clazz) {
        return Convert.convert(clazz, str);
    }

}
