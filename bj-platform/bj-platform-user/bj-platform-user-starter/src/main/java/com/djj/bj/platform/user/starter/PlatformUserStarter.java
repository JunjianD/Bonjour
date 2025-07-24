package com.djj.bj.platform.user.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 平台用户启动类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.starter.config
 * @className PlatformUserStarter
 * @date 2025/7/23 10:29
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = {"com.djj.bj.platform.user.domain.repository"})
@ComponentScan(basePackages = {"com.djj.bj"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PlatformUserStarter {
    public static void main(String[] args) {
        SpringApplication.run(PlatformUserStarter.class, args);
    }
}
