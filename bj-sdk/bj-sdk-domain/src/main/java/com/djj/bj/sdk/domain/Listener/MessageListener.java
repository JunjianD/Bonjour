package com.djj.bj.sdk.domain.Listener;

import com.djj.bj.common.io.model.SendResult;

/**
 * 消息监听器接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.domain.Listener
 * @interfaceName MessageListener
 * @date 2025/7/12 15:19
 */
public interface MessageListener<T> {
    /**
     * 处理发送结果
     *
     * @param result 发送结果
     */
    void doProcess(SendResult<T> result);
}
