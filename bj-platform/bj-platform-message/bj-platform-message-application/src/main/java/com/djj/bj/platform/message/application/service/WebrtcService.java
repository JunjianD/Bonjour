package com.djj.bj.platform.message.application.service;

import com.djj.bj.platform.message.application.ice.ICEServer;

import java.util.List;

/**
 * WebRTC Service Interface
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service
 * @interfaceName WebrtcService
 * @date 2025/8/10 20:30
 */
public interface WebrtcService {
    /**
     * 视频呼叫
     *
     * @param uid   ID
     * @param offer SDP offer字符串
     */
    void call(Long uid, String offer);

    /**
     * 取消呼叫
     *
     * @param uid 被呼叫者的ID
     */
    void cancel(Long uid);

    /**
     * 呼叫失败处理
     *
     * @param uid    被呼叫者的ID
     * @param reason 失败原因描述
     */
    void failed(Long uid, String reason);

    /**
     * 接受视频通话请求
     *
     * @param uid    被呼叫者的ID
     * @param answer SDP answer字符串
     */
    void accept(Long uid, String answer);

    /**
     * 拒绝视频通话请求
     *
     * @param uid 被呼叫者的ID
     */
    void reject(Long uid);

    /**
     * 挂断视频通话
     *
     * @param uid 被呼叫者的ID
     */
    void leave(Long uid);

    /**
     * 同步ICE候选者信息
     *
     * @param uid       被呼叫者的ID
     * @param candidate ICE候选者字符串
     */
    void candidate(Long uid, String candidate);

    /**
     * 获取ICE服务列表
     */
    List<ICEServer> getIceServers();
}
