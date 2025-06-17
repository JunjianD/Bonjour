package com.djj.bj.infrastructure.holder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringContextHolder
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.infrastructure.holder
 * @className SpringContextHolder
 * @date 2025/6/17 22:03
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.ctx = applicationContext;
    }
    private static void checkContext() {
        if (ctx == null) {
            throw new RuntimeException("ApplicationContext is null, please check if Spring is configured correctly.");
        }
    }

    public static ApplicationContext getApplicationContext() {
        checkContext();
        return ctx;
    }

    public static <T> T getBean(String beanName) {
        checkContext();
        return (T) ctx.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType) {
        checkContext();
        return ctx.getBean(requiredType);
    }

    public static <T> T getBean(String beanName, Class<T> required){
        checkContext();
        return ctx.getBean(beanName, required);
    }
}
