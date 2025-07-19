package com.djj.bj.platform.common.risk.rule.service.base;

import com.djj.bj.platform.common.risk.rule.service.RuleChainService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String SEPARATOR = ",";

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
    protected String getIP(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = request.getHeader("x-Forwarded-For");

        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase(UNKNOWN)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase(UNKNOWN)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
//        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase(UNKNOWN)) {
//            ip = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase(UNKNOWN)) {
//            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase(UNKNOWN)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase(UNKNOWN)) {
            ip = request.getRemoteAddr();
            if (LOCALHOST_IP.equalsIgnoreCase(ip) || LOCALHOST_IPV6.equalsIgnoreCase(ip)) {
                // 根据网卡取本机配置的 IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("BaseRuleChainService.getIP | 获取客户端IP地址异常 | {}", e.getMessage());
                }
                if (inetAddress != null) {
                    ip = inetAddress.getHostAddress();
                }
            }
        }
        // 对于通过多个代理的情况，分割出第一个 IP
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(SEPARATOR) > 0) {
                ip = ip.substring(0, ip.indexOf(SEPARATOR));
            }
        }
        return LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IP : ip;
    }

}
