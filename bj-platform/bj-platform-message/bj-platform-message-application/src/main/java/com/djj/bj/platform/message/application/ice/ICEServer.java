package com.djj.bj.platform.message.application.ice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ICEServer类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.ice
 * @className ICEServer
 * @date 2025/8/10 20:34
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ICEServer {
    private String urls;
    private String username;
    private String credential;
}
