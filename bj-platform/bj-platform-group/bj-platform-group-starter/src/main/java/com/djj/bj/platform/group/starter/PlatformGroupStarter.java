package com.djj.bj.platform.group.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 平台群组服务启动类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group
 * @className PlatFormGroupStarter
 * @date 2025/8/4 10:25
 */
@EnableDubbo
@EnableDiscoveryClient
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = {"com.djj.bj.platform.group.domain.repository"})
@ComponentScan(basePackages = {"com.djj.bj"})
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
})
public class PlatformGroupStarter {
    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(PlatformGroupStarter.class, args);
    }
}
