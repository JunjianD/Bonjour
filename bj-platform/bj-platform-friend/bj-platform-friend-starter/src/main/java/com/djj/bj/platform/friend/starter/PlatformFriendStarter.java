package com.djj.bj.platform.friend.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 平台好友服务启动类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.starter
 * @className PlatformFriendStarter
 * @date 2025/8/1 17:33
 */
@EnableDubbo
@EnableDiscoveryClient
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = {"com.djj.bj.platform.friend.domain.repository"})
@ComponentScan(basePackages = {"com.djj.bj"})
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
})
public class PlatformFriendStarter {
    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(PlatformFriendStarter.class, args);
    }
}
