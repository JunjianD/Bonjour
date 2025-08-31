package com.djj.bj.server.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 后端服务启动类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.server.starter
 * @className ServerStarter
 * @date 2025/8/13 19:56
 */
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@ComponentScan(basePackages = {"com.djj.bj"})
@SpringBootApplication
public class ServerStarter {
    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(ServerStarter.class, args);
    }
}
