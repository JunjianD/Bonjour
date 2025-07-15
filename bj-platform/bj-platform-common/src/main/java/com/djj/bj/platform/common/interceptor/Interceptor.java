package com.djj.bj.platform.common.interceptor;

import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.interceptor.base.BaseInterceptor;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.risk.rule.service.RuleChainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 统一拦截器类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.interceptor
 * @className Interceptor
 * @date 2025/7/15 10:07
 */
@Component
public class Interceptor extends BaseInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取排序规则链
        List<RuleChainService> ruleChainServices = this.getRuleChainServices();

        // 遍历规则链并执行
        for (RuleChainService ruleChainService : ruleChainServices) {
            // 执行规则链中的每个规则
            HttpCode httpCode = ruleChainService.execute(request, handler);
            // 如果执行结果不是成功状态码，则抛出异常
            if (!HttpCode.SUCCESS.getCode().equals(httpCode.getCode())) {
                throw new BJException(httpCode);
            }
        }
        return true;
    }
}
