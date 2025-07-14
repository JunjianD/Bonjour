package com.djj.bj.platform.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 缓存过滤器类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.filter
 * @className CacheFilter
 * @date 2025/7/14 21:18
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/*", filterName = "xssFilter")
public class CacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new CacheHttpServletRequestWrapper((HttpServletRequest) servletRequest), servletResponse);
    }
}
