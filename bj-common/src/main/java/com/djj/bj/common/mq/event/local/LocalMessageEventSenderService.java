package com.djj.bj.common.mq.event.local;

import com.djj.bj.common.io.model.BasicMessage;
import com.djj.bj.common.mq.event.MessageEventSenderService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 本地消息事件发送服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.mq.event.local
 * @className LocalMessageEventSenderService
 * @date 2025/7/25 17:07
 */
@Component
@ConditionalOnProperty(name = "message.mq.event.type", havingValue = "local")
public class LocalMessageEventSenderService implements MessageEventSenderService {
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public boolean send(BasicMessage message) {
        eventPublisher.publishEvent(message);
        return true;
    }
}
