package com.djj.bj.sdk.infrastructure.multicaster.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.sdk.domain.Listener.MessageListener;
import com.djj.bj.sdk.domain.annotation.Listening;
import com.djj.bj.sdk.infrastructure.multicaster.MessageListenerMulticaster;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 默认消息监听器广播实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.infrastructure.multicaster.impl
 * @className DefaultMessageListenerMulticaster
 * @date 2025/7/12 15:29
 */
@Component
public class DefaultMessageListenerMulticaster implements MessageListenerMulticaster {

    private final List<MessageListener> messageListenerList;

    public DefaultMessageListenerMulticaster(List<MessageListener> messageListenerList) {
        this.messageListenerList = messageListenerList;
    }

    // 原本的写法，但是resource不支持非空，因此改为上面的构造器注入法，能够自动注入非空的messageListenerList或者空List
//    @Resource
//    private List<MessageListener> messageListenerList = Collections.emptyList();

    @Override
    public <T> void multicast(ListeningType listeningType, SendResult<T> result) {
        if (CollectionUtil.isEmpty(messageListenerList)) {
            return;
        }
        messageListenerList.forEach(messageListener -> {
            Listening isListening = messageListener.getClass().getAnnotation(Listening.class);
            // 非空且监听类型匹配全局或指定类型
            if (isListening != null && (ListeningType.ALL_MESSAGE.equals(isListening.listeningType()) || isListening.listeningType().equals(listeningType))) {
                if (result.getData() instanceof JSONObject data) {
                    Type superInterface = messageListener.getClass().getGenericInterfaces()[0];
                    Type type = ((ParameterizedType) superInterface).getActualTypeArguments()[0];
                    // fastjson2转换类型
                    result.setData(data.to(type));
                }
                // 执行监听器的处理方法
                messageListener.doProcess(result);
            }
        });
    }
}
