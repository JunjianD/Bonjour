package com.djj.bj.platform.common.risk.rule.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.risk.enums.RuleEnum;
import com.djj.bj.platform.common.risk.rule.service.base.BaseRuleChainService;
import com.djj.bj.platform.common.utils.XssUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * xss规则链服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.rule.service.impl
 * @className XssRuleChainService
 * @date 2025/7/15 15:07
 */
@Component
public class XssRuleChainService extends BaseRuleChainService {
    private static final Logger logger = LoggerFactory.getLogger(XssRuleChainService.class);

    @Value("{bj.rule.xssRule.enabled}")
    private Boolean xssRuleEnabled;

    @Value("{bj.rule.xssRule.order}")
    private Integer xssRuleOrder;

    @Override
    public String getServiceName() {
        return RuleEnum.XSS.getMessage();
    }

    @Override
    public HttpCode execute(HttpServletRequest request, Object handler) {
        // 未开启xss，直接通过校验
        if (BooleanUtil.isFalse(xssRuleEnabled)) {
            return HttpCode.SUCCESS;
        }
        // 否则，检查参数，执行逻辑
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String[] values : paramMap.values()) {
            for (String value : values) {
                if (XssUtils.checkXss(value)) {
                    return HttpCode.XSS_PARAM_ERROR;
                }
            }
        }
        // 检查body
        String body = getBody(request);
        if (XssUtils.checkXss(body)) {
            return HttpCode.XSS_PARAM_ERROR;
        }
        return HttpCode.SUCCESS;
    }

    @Override
    public int getOrder() {
        return xssRuleOrder == null ? RuleEnum.XSS.getCode() : xssRuleOrder;
    }

    private String getBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.error("XssRuleChainService.getBody | 获取请求体异常: {}", e.getMessage());
        }
        return sb.toString();
    }
}
