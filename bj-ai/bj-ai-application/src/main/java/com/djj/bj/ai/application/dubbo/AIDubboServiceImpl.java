package com.djj.bj.ai.application.dubbo;

import com.djj.bj.ai.common.constants.AIConstants;
import com.djj.bj.ai.domain.service.AIInteractiveService;
import com.djj.bj.ai.dubbo.service.AIDubboService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Dubbo接口实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.application.dubbo
 * @className AIDubboServiceImpl
 * @date 2026/3/5 23:12
 */
@Component
@DubboService(version = AIConstants.DEFAULT_DUBBO_VERSION, timeout = 60000)
public class AIDubboServiceImpl implements AIDubboService {
    @Resource
    private AIInteractiveService aiInteractiveService;

    @Override
    public String sendMessage(String requestData) throws IOException {
        return aiInteractiveService.sendMessage(requestData);
    }
}
