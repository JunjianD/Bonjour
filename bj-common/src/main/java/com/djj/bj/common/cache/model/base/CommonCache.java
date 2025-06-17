package com.djj.bj.common.cache.model.base;

import lombok.Setter;

import java.io.Serializable;

/**
 * 通用缓存模型
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.model.base
 * @className CommonCache
 * @date 2025/6/9 19:12
 */
@Setter
public class CommonCache implements Serializable {
    private static final long serialVersionUID = 186993067788152652L;
    // 缓存数据是否存在
    protected boolean exist;
    // 缓存版本号
    protected Long version;
    // 稍后再试
    protected boolean retryLater;

    public boolean isExist() {
        return exist;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isRetryLater() {
        return retryLater;
    }
}
