package com.djj.bj.platform.common.risk.rule.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.risk.enums.RuleEnum;
import com.djj.bj.platform.common.risk.rule.service.base.BaseRuleChainService;
import com.djj.bj.platform.common.risk.window.SlidingWindowLimitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * IP规则链服务实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.rule.service.impl
 * @className IPRuleChainService
 * @date 2025/7/19 20:31
 */
@Component
public class IPRuleChainService extends BaseRuleChainService {
    private final Logger logger = LoggerFactory.getLogger(IPRuleChainService.class);

    private static final int DEFAULT_WINDOWS_SIZE = 50;
    private static final int DEFAULT_WINDOWS_PERIOD = 1000;

    @Value("${bj.rule.ipRule.enabled}")
    private Boolean ipRuleEnabled;

    @Value("${bj.rule.ipRule.order}")
    private Integer ipRuleOrder;

    @Value("${bj.rule.ipRule.windowsSize}")
    private Integer windowsSize;

    @Value("${bj.rule.ipRule.windowPeriod}")
    private Long windowPeriod;

    @Resource
    private SlidingWindowLimitService slidingWindowLimitService;

    @Override
    public String getServiceName() {
        return RuleEnum.IP.getMessage();
    }

    @Override
    public HttpCode execute(HttpServletRequest request, Object handler) {
        if (BooleanUtil.isFalse(ipRuleEnabled)) {
            return HttpCode.SUCCESS;
        }
        try {
            windowsSize = windowsSize == null ? DEFAULT_WINDOWS_SIZE : windowsSize;
            windowPeriod = windowPeriod == null ? DEFAULT_WINDOWS_PERIOD : windowPeriod;
            String ip = this.getIP(request);
            boolean result = slidingWindowLimitService.passThough(ip, windowPeriod, windowsSize);
            return result ? HttpCode.SUCCESS : HttpCode.PROGRAM_ERROR;

        } catch (Exception e) {
            logger.error("IPRuleChainService | IP限制异常 | {}", e.getMessage());
            return HttpCode.PROGRAM_ERROR;
        }
    }

    @Override
    public int getOrder() {
        return ipRuleOrder == null ? RuleEnum.IP.getCode() : ipRuleOrder;
    }
}
