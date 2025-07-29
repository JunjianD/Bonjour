package com.djj.bj.platform.common.risk.rule.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.risk.enums.RuleEnum;
import com.djj.bj.platform.common.risk.rule.service.base.BaseRuleChainService;
import com.djj.bj.platform.common.risk.window.SlidingWindowLimitService;
import com.djj.bj.platform.common.session.UserSession;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 资源访问限制
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.rule.service.impl
 * @className PathRuleChainService
 * @date 2025/7/21 20:47
 */
@Component
public class PathRuleChainService extends BaseRuleChainService {
    private final Logger logger = LoggerFactory.getLogger(PathRuleChainService.class);

    @Value("${bj.rule.pathRule.enabled}")
    private Boolean pathRuleEnabled;

    @Value("${bj.rule.pathRule.order}")
    private Integer pathRuleOrder;

    @Value("${bj.rule.pathRule.windowsSize}")
    private Integer windowsSize;

    @Value("${bj.rule.pathRule.windowPeriod}")
    private Long windowPeriod;

    @Resource
    private SlidingWindowLimitService slidingWindowLimitService;

    @Override
    public String getServiceName() {
        return RuleEnum.PATH.getMessage();
    }

    @Override
    public HttpCode execute(HttpServletRequest request, Object handler) {
        if (BooleanUtil.isFalse(pathRuleEnabled)) {
            return HttpCode.SUCCESS;
        }
        try {
            UserSession userSession = this.getUserSessionWithoutException(request);
            if (userSession == null) {
                return HttpCode.SUCCESS;
            }
            windowsSize = windowsSize == null ? DEFAULT_WINDOWS_SIZE : windowsSize;
            windowPeriod = windowPeriod == null ? DEFAULT_WINDOWS_PERIOD : windowPeriod;
            String path = request.getServletPath().concat(String.valueOf(userSession.getUserId())).concat(String.valueOf(userSession.getTerminalType()));
            boolean result = slidingWindowLimitService.passThough(path, windowPeriod, windowsSize);
            return result ? HttpCode.SUCCESS : HttpCode.PROGRAM_ERROR;
        } catch (Exception e) {
            logger.error("PathRuleChainService | 资源访问限制异常 | {}", e.getMessage());
            return HttpCode.PROGRAM_ERROR;
        }
    }

    @Override
    public int getOrder() {
        return pathRuleOrder == null ? RuleEnum.PATH.getCode() : pathRuleOrder;
    }
}
