package com.djj.bj.platform.common.risk.rule.service.base;

import com.djj.bj.platform.common.risk.rule.service.RuleChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 基础规则链服务抽象类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.rule.service.base
 * @className BaseRuleChainService
 * @date 2025/7/15 09:34
 */
public abstract class BaseRuleChainService implements RuleChainService {
    private final Logger logger = LoggerFactory.getLogger(BaseRuleChainService.class);

    /**
     * 当前服务名称
     *
     * @return 服务名称
     */
    public abstract String getServiceName();

    public BaseRuleChainService() {
        logger.info("BaseRuleChainService | 当前规则服务 | {}", this.getServiceName());
    }

    /**
     * 获取请求的IP地址
     *
     * @param request ServerHttpRequest 请求对象
     * @return String 返回IP地址
     */
    protected String getIP(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !ip.equalsIgnoreCase("unknown")) {
            // 多次反向代理后会有多个ip值，第一个ip为真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip.replaceAll(":", ".");
    }

}
