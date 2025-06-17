package com.djj.bj.common.cache.model;

import com.djj.bj.common.cache.model.base.CommonCache;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据缓存
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.cache.model
 * @className BusinessCache
 * @date 2025/6/9 19:21
 */
@Setter
@Getter
public class BusinessCache<T> extends CommonCache {
    private T data;

    public BusinessCache<T> with(T data) {
        this.data = data;
        this.exist = true;
        return this;
    }

    public BusinessCache<T> withVersion(Long version) {
        this.version = version;
        return this;
    }

    public BusinessCache<T> retryLater() {
        this.retryLater = true;
        return this;
    }

    public BusinessCache<T> notExist() {
        this.exist = false;
        this.version = -1L;
        return this;
    }

}
