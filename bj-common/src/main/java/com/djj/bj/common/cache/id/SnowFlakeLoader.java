package com.djj.bj.common.cache.id;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 便于加载配置参数
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.id
 * @className SnowFlakeLoader
 * @date 2025/6/12 22:14
 */
public class SnowFlakeLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowFlakeLoader.class);

    public static final String DATA_CENTER_ID = "data.center.id";
    public static final String MACHINE_ID = "machine.id";

    private volatile static Properties properties;

    static {
        properties = new Properties();
        InputStream in = SnowFlakeLoader.class.getClassLoader().getResourceAsStream("properties/snowflake/snowflake.properties");
        try{
            properties.load(in);
        }catch (IOException e){
            LOGGER.error("SnowFlakeLoader | load properties error. ", e);
        }
    }

    private static String getStringValue(String key) {
        if (properties == null) return "";
        return properties.getProperty(key, "");
    }

    private static @NotNull Long getLongValue(String key) {
        String value = getStringValue(key);
        return (value == null || value.trim().isEmpty()) ? 0 : Long.parseLong(value);
    }


    public static @NotNull Long getDataCenterId() {
        return getLongValue(DATA_CENTER_ID);
    }

    public static @NotNull Long getMachineId() {
        return getLongValue(MACHINE_ID);
    }
}
