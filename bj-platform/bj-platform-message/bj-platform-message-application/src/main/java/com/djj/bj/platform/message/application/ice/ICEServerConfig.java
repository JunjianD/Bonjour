package com.djj.bj.platform.message.application.ice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ICE服务配置
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.ice
 * @className ICEServerConfig
 * @date 2025/8/10 20:34
 */
@Component
@ConfigurationProperties(prefix = "webrtc")
@Getter
@Setter
public class ICEServerConfig {
    private List<ICEServer> iceServers = new ArrayList<>();
}
