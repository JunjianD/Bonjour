package com.djj.bj.ai.starter.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * 跨域配置
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.starter.config
 * @className CorsConfig
 * @date 2026/3/4 23:57
 */
@Configuration
public class CorsConfig {
    @Value("${cors.allowed-origin-patterns:${BJ_CORS_ALLOWED_ORIGIN_PATTERNS:http://localhost:8080,http://localhost:8081,http://222.186.48.169,http://222.186.48.169:8898}}")
    private String allowedOriginPatterns;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean = new FilterRegistrationBean<>();
        //添加CORS配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许的域名模式通过配置中心或环境变量收敛，避免携带凭证时放开任意来源。
        corsConfiguration.setAllowedOriginPatterns(Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        //允许的头信息
        corsConfiguration.addAllowedHeader("*");
        //允许的请求方式
        corsConfiguration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "OPTIONS", "DELETE"));
        //是否发送cookie信息
        corsConfiguration.setAllowCredentials(true);
        //预检请求的有效期，单位为秒
        corsConfiguration.setMaxAge(3600L);

        //添加映射路径，标识待拦截的请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        corsFilterFilterRegistrationBean.setFilter(new CorsFilter(source));
        corsFilterFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsFilterFilterRegistrationBean;
    }
}
