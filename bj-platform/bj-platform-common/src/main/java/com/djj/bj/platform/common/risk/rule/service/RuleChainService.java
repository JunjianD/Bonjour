package com.djj.bj.platform.common.risk.rule.service;

import com.djj.bj.platform.common.model.enums.HttpCode;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 规则调用链接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.rule.service
 * @interfaceName RuleChainService
 * @date 2025/7/15 09:29
 */
public interface RuleChainService {
    /**
     * 执行处理逻辑
     *
     * @param request HttpServletRequest 请求对象
     * @param handler 处理器对象
     * @return HttpCode 执行结果的HTTP状态码
     */
    HttpCode execute(HttpServletRequest request, Object handler);

    /**
     * 规则链中的每个规则排序
     *
     * @return int 规则的执行顺序
     */
    int getOrder();
}
