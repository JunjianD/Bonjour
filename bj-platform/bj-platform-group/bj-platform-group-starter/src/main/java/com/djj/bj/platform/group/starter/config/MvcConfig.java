package com.djj.bj.platform.group.starter.config;

import com.djj.bj.platform.common.interceptor.Interceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.config
 * @className MvcConfig
 * @date 2025/8/4 12:04
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/login", "/logout", "/register", "/refreshToken",
                        "/swagger-resources/**", "/webjars/**", "/swagger-ui/**",
                        "/v3/api-docs/**"); // 排除登录、注册和登出接口
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //使用BCryptPasswordEncoder进行密码加密
        return new BCryptPasswordEncoder();
    }
}
