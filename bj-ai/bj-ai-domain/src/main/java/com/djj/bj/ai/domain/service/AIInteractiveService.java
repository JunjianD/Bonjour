package com.djj.bj.ai.domain.service;

import java.io.IOException;

/**
 * 对接大模型的domain接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.domain.service
 * @interfaceName AIInteractiveService
 * @date 2026/3/4 22:32
 */
public interface AIInteractiveService {
    /**
     * 往大模型发送数据，并接受返回结果
     *
     * @param requestData 发送的数据
     * @return 大模型返回的结果
     * @throws IOException IO异常
     */
    String sendMessage(String requestData) throws IOException;

    /**
     * 往大模型发送带会话上下文的数据，并接受返回结果
     *
     * @param conversationId 会话id
     * @param userId         当前用户id
     * @param userName       当前用户名/昵称
     * @param requestData    本次发送的数据
     * @return 大模型返回结果
     * @throws IOException IO异常
     */
    default String sendMessage(String conversationId, Long userId, String userName, String requestData) throws IOException {
        return sendMessage(requestData);
    }
}
