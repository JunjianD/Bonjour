package com.djj.bj.platform.common.interceptor.base;

import cn.hutool.core.collection.CollectionUtil;
import com.djj.bj.platform.common.risk.rule.service.RuleChainService;
import jakarta.annotation.Resource;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础拦截器类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.interceptor.base
 * @className BaseInterceptor
 * @date 2025/7/15 10:06
 */
public abstract class BaseInterceptor implements HandlerInterceptor {

    @Resource
    private List<RuleChainService> ruleChainServices;

    protected List<RuleChainService> getRuleChainServices() {
        if (CollectionUtil.isEmpty(ruleChainServices)) {
            return Collections.emptyList();
        }
        return ruleChainServices.stream().sorted(Comparator.comparing(RuleChainService::getOrder)).collect(Collectors.toList());
    }
}
