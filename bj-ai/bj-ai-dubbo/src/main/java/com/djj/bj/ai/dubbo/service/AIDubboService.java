package com.djj.bj.ai.dubbo.service;

import java.io.IOException;

/**
 * AI服务对外提供Dubbo接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.dubbo.service
 * @interfaceName AIDubboService
 * @date 2026/3/4 23:47
 */
public interface AIDubboService {
    /**
     * 往大模型发送数据，并接受返回结果
     *
     * @param requestData 发送的数据
     * @return 大模型返回的结果
     * @throws IOException IO异常
     */
    String sendMessage(String requestData) throws IOException;

    /**
     * 带会话上下文地发送消息
     *
     * @param conversationId 会话id
     * @param userId         当前用户id
     * @param userName       当前用户名/昵称
     * @param requestData    消息内容
     * @return 大模型返回结果
     * @throws IOException IO异常
     */
    String sendMessage(String conversationId, Long userId, String userName, String requestData) throws IOException;
}
