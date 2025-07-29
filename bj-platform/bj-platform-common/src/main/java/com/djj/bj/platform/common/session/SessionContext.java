package com.djj.bj.platform.common.session;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 会话上下文类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.session
 * @className SessionContext
 * @date 2025/7/14 16:21
 */
public class SessionContext {
    @Nullable
    public static UserSession getUserSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        if (request != null) {
            return (UserSession) request.getAttribute(PlatformConstants.SESSION);
        }
        return null;
    }
}
