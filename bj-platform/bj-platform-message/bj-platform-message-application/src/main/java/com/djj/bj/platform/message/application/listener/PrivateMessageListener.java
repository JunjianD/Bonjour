package com.djj.bj.platform.message.application.listener;

import com.alibaba.fastjson2.JSON;
import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.sdk.domain.Listener.MessageListener;
import com.djj.bj.sdk.domain.annotation.Listening;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监听私聊消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.listener
 * @className PrivateMessageListener
 * @date 2025/8/9 22:50
 */
@Listening(listeningType = ListeningType.PRIVATE_MESSAGE)
public class PrivateMessageListener implements MessageListener<PrivateMessageVO> {

    private final Logger logger = LoggerFactory.getLogger(PrivateMessageListener.class);

    @Override
    public void doProcess(SendResult<PrivateMessageVO> result) {
        logger.info("PrivateMessageListener | 监听到单聊消息数据 | {}", JSON.toJSONString(result));
    }
}
