package com.djj.bj.platform.common.risk.rule.service.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.djj.bj.common.io.jwt.JwtUtils;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.jwt.JwtProperties;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.risk.rule.service.RuleChainService;
import com.djj.bj.platform.common.session.UserSession;
import jakarta.annotation.Resource;
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

    @Resource
    private JwtProperties jwtProperties;

    protected static final int DEFAULT_WINDOWS_SIZE = 50;
    protected static final int DEFAULT_WINDOWS_PERIOD = 1000;

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

    /**
     * 从请求中获取 UserSession
     * 如果获取失败，返回 null
     *
     * @param request HttpServletRequest 请求对象
     * @return UserSession 用户会话信息
     */
    protected UserSession getUserSessionWithoutException(HttpServletRequest request) {
        // 从 http 请求头中取出 token
        String token = request.getHeader(PlatformConstants.ACCESS_TOKEN);
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        // 验证 token 并获取 UserSession
        if (!JwtUtils.checkSign(token, jwtProperties.getAccessTokenSecret())) {
            return null;
        }
        String strJson = JwtUtils.getInfo(token);
        if (StrUtil.isEmpty(strJson)) {
            return null;
        }
        return JSON.parseObject(strJson, UserSession.class);
    }

    /**
     * 获取 UserSession
     * 如果未登录或 token 无效，抛出异常
     *
     * @param request HttpServletRequest 请求对象
     * @return UserSession 用户会话信息
     */
    protected UserSession getUserSession(HttpServletRequest request) {
        String token = request.getHeader(PlatformConstants.ACCESS_TOKEN);
        if (StrUtil.isEmpty(token)) {
            logger.error("BaseRuleChainService | 未登录，url | {}", request.getRequestURI());
            throw new BJException(HttpCode.NO_LOGIN);
        }
        // 验证 token
        if (!JwtUtils.checkSign(token, jwtProperties.getAccessTokenSecret())) {
            logger.error("BaseRuleChainService | token已失效，url | {}", request.getRequestURI());
            throw new BJException(HttpCode.INVALID_TOKEN);
        }
        // 存放 session
        String strJson = JwtUtils.getInfo(token);
        if (StrUtil.isEmpty(strJson)) {
            logger.error("BaseRuleChainService | token已失效，url | {}", request.getRequestURI());
            throw new BJException(HttpCode.INVALID_TOKEN);
        }
        return JSON.parseObject(strJson, UserSession.class);
    }

}
