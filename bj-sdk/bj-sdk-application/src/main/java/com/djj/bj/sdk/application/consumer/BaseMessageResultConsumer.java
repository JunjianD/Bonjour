package com.djj.bj.sdk.application.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.SendResult;

/**
 * 基础返回结果消费者类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.application.consumer
 * @className BaseMessageResultConsumer
 * @date 2025/7/12 18:39
 */
public class BaseMessageResultConsumer {
    protected SendResult<?> getResultMessage(String message) {
        // 解析消息字符串为 SendResult 对象
        JSONObject result = JSONObject.parseObject(message);
        String eventStr = result.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SendResult.class);
    }
}
