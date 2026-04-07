package com.djj.bj.ai.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 启动类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.starter
 * @className AIStarter
 * @date 2026/3/4 23:57
 */
@EnableDubbo
@EnableDiscoveryClient
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan(basePackages = {"com.djj.bj.ai"})
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
})
public class AIStarter {
    public static void main(String[] args) {
        SpringApplication.run(AIStarter.class, args);
    }
}
