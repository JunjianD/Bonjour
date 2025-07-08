package com.djj.bj.server.application.consumer;

import com.alibaba.fastjson.JSONObject;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.ReceiveMessage;

/**
 * 基础消息消费者类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.consumer
 * @className BasicMessageConsumer
 * @date 2025/7/7 17:20
 */
public class BasicMessageConsumer {
    protected ReceiveMessage getReceiveMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(Constants.MSG_KEY);
        return JSONObject.parseObject(eventStr, ReceiveMessage.class);
    }
}
