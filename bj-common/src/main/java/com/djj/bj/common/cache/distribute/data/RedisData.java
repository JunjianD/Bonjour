package com.djj.bj.common.cache.distribute.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用于表示缓存到Redis中的数据
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.distribute.data
 * @className RedisData
 * @date 2025/6/3 21:14
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedisData {
    private Object data; // 实际业务数据
    private LocalDateTime expireTime; // 过期时间点
}
