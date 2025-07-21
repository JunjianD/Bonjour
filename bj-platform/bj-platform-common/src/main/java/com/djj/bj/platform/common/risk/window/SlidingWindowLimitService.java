package com.djj.bj.platform.common.risk.window;

/**
 * 滑动窗口限流服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.risk.window
 * @interfaceName SlidingWindowLimitService
 * @date 2025/7/19 16:23
 */
public interface SlidingWindowLimitService {
    /**
     * 是否能通过滑动窗口的验证
     *
     * @param key          事件标识
     * @param windowPeriod 窗口限流的周期，单位是毫秒
     * @param windowSize   滑动窗口大小
     * @return 是否通过
     */
    boolean passThough(String key, long windowPeriod, int windowSize);
}
