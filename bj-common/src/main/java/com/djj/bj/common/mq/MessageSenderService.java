package com.djj.bj.common.mq;

import com.djj.bj.common.io.model.BasicMessage;
import org.apache.rocketmq.client.producer.TransactionSendResult;

/**
 * 消息发送服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.mq
 * @interfaceName MessageSenderService
 * @date 2025/6/10 21:23
 */
public interface MessageSenderService {
    /**
     * 发送消息
     *
     * @param message 发送的消息
     * @return 是否发送成功
     */
    boolean send(BasicMessage message);

    /**
     * 发送事务消息，用于RocketMQ
     *
     * @param message 事务消息
     * @param arg     其他参数
     * @return 返回事务发送结果
     */
    default TransactionSendResult sendMessageInTransaction(BasicMessage message, Object arg) {
        return null;
    }
}
