package com.djj.bj.common.mq.event;

import com.djj.bj.common.io.model.BasicMessage;
import org.apache.rocketmq.client.producer.TransactionSendResult;

/**
 * 服务事件发送器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.mq.event
 * @interfaceName MessageEventSenderService
 * @date 2025/7/25 16:46
 */
public interface MessageEventSenderService {

    /**
     * 发送消息
     *
     * @param message 消息对象
     * @return 是否发送成功
     */
    boolean send(BasicMessage message);

    default TransactionSendResult sendMessageInTransaction(BasicMessage message, Object arg) {
        return null;
    }
}
