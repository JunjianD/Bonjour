package com.djj.bj.platform.common.risk.rule.service.impl;

import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.risk.enums.RuleEnum;
import com.djj.bj.platform.common.risk.rule.service.base.BaseRuleChainService;
import com.djj.bj.platform.common.session.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

/**
 * 账号安全校验规则
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.rule.service.impl
 * @className AuthRuleChainService
 * @date 2025/7/21 22:05
 */
@Component
public class AuthRuleChainService extends BaseRuleChainService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${bj.rule.authRule.order}")
    private Integer authRuleOrder;

    @Override
    public String getServiceName() {
        return RuleEnum.AUTH.getMessage();
    }

    @Override
    public HttpCode execute(HttpServletRequest request, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return HttpCode.SUCCESS;
        }
        UserSession userSession = this.getUserSession(request);
        if (userSession == null) {
            throw new BJException(HttpCode.NO_LOGIN);
        }
        request.setAttribute(PlatformConstants.SESSION, userSession);
        return HttpCode.SUCCESS;
    }

    @Override
    public int getOrder() {
        return authRuleOrder == null ? RuleEnum.AUTH.getCode() : authRuleOrder;
    }
}
