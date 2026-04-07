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
 * @date 2026/3/6 21:21
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
}
