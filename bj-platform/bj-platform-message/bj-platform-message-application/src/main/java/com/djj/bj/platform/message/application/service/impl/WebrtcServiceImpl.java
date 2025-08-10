package com.djj.bj.platform.message.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.PrivateChat;
import com.djj.bj.common.io.model.UserInfo;
import com.djj.bj.platform.common.exception.BJException;
import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.enums.HttpCode;
import com.djj.bj.platform.common.model.enums.MessageType;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.common.session.WebrtcSession;
import com.djj.bj.platform.message.application.ice.ICEServer;
import com.djj.bj.platform.message.application.ice.ICEServerConfig;
import com.djj.bj.platform.message.application.service.WebrtcService;
import com.djj.bj.sdk.core.client.Client;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * WebRTC Service实现类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.service.impl
 * @className WebrtcServiceImpl
 * @date 2025/8/10 20:32
 */
@Service
public class WebrtcServiceImpl implements WebrtcService {
    @Resource
    private Client client;

    @Resource
    private DistributeCacheService distributeCacheService;

    @Resource
    private ICEServerConfig iceServerConfig;

    @Override
    public void call(Long uid, String offer) {
        UserSession session = SessionContext.getUserSession();
        if (!client.isOnline(uid)) {
            throw new BJException("对方目前不在线");
        }
        // 创建webrtc会话
        WebrtcSession webrtcSession = new WebrtcSession();
        webrtcSession.setCallerId(session.getUserId());
        webrtcSession.setCallerTerminal(session.getTerminalType());
        String key = getSessionKey(session.getUserId(), uid);
        distributeCacheService.set(key, webrtcSession, PlatformConstants.WEBRTC_SESSION_CACHE_EXPIRE, TimeUnit.HOURS);
        // 向对方所有终端发起呼叫
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_CALL.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());
        messageInfo.setContent(offer);

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        sendMessage.setSendToSelfOtherTerminals(false);
        sendMessage.setReturnResult(false);
        sendMessage.setContent(messageInfo);
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public void cancel(Long uid) {
        if (uid == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        // 删除会话信息
        this.removeWebrtcSession(session.getUserId(), uid);
        // 向对方所有终端推送取消通话信令
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_ACCEPT.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        sendMessage.setSendToSelfOtherTerminals(false);
        sendMessage.setReturnResult(false);
        sendMessage.setContent(messageInfo);
        // 通知对方取消会话
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public void failed(Long uid, String reason) {
        if (uid == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        // 查询webrtc会话
        WebrtcSession webrtcSession = getWebrtcSession(session.getUserId(), uid);
        // 删除会话信息
        this.removeWebrtcSession(uid, session.getUserId());
        // 向发起方推送通话失败信令
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_FAILED.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        // 告知其他终端已经会话失败,中止呼叫
        sendMessage.setSendToSelfOtherTerminals(true);
        sendMessage.setReturnResult(false);
        sendMessage.setReceiverTerminals(Collections.singletonList(webrtcSession.getCallerTerminal()));
        sendMessage.setContent(messageInfo);
        // 通知对方取消会话
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public void accept(Long uid, String answer) {
        UserSession session = SessionContext.getUserSession();
        if (uid == null || StrUtil.isEmpty(answer)) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        // 查询webrtc会话
        WebrtcSession webrtcSession = getWebrtcSession(session.getUserId(), uid);
        // 更新接受者信息
        webrtcSession.setAcceptorId(session.getUserId());
        webrtcSession.setAcceptorTerminal(session.getTerminalType());
        String key = getSessionKey(session.getUserId(), uid);
        distributeCacheService.set(key, webrtcSession, PlatformConstants.WEBRTC_SESSION_CACHE_EXPIRE, TimeUnit.HOURS);
        // 向发起人推送接受通话信令
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_ACCEPT.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());
        messageInfo.setContent(answer);

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        // 告知其他终端已经接受会话,中止呼叫
        sendMessage.setSendToSelfOtherTerminals(true);
        sendMessage.setReturnResult(false);
        sendMessage.setReceiverTerminals((Collections.singletonList(webrtcSession.getCallerTerminal())));
        sendMessage.setContent(messageInfo);
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public void reject(Long uid) {
        if (uid == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        // 查询webrtc会话
        WebrtcSession webrtcSession = getWebrtcSession(session.getUserId(), uid);
        // 删除会话信息
        removeWebrtcSession(uid, session.getUserId());
        // 向发起人推送拒绝通话信令
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_REJECT.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        // 告知其他终端已经拒绝会话,中止呼叫
        sendMessage.setSendToSelfOtherTerminals(true);
        sendMessage.setReturnResult(false);
        sendMessage.setReceiverTerminals(Collections.singletonList(webrtcSession.getCallerTerminal()));
        sendMessage.setContent(messageInfo);
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public void leave(Long uid) {
        UserSession session = SessionContext.getUserSession();
        // 查询webrtc会话
        WebrtcSession webrtcSession = getWebrtcSession(session.getUserId(), uid);
        // 删除会话信息
        removeWebrtcSession(uid, session.getUserId());
        // 向对方推送挂断通话信令
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_HANDUP.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        sendMessage.setSendToSelfOtherTerminals(false);
        sendMessage.setReturnResult(false);
        Integer terminal = this.getTerminalType(uid, webrtcSession);
        sendMessage.setReceiverTerminals(Collections.singletonList(terminal));
        sendMessage.setContent(messageInfo);
        // 通知对方取消会话
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public void candidate(Long uid, String candidate) {
        if (uid == null) {
            throw new BJException(HttpCode.PARAMS_ERROR);
        }
        UserSession session = SessionContext.getUserSession();
        // 查询webrtc会话
        WebrtcSession webrtcSession = getWebrtcSession(session.getUserId(), uid);
        // 向发起方推送同步candidate信令
        PrivateMessageVO messageInfo = new PrivateMessageVO();
        messageInfo.setType(MessageType.RTC_CANDIDATE.getCode());
        messageInfo.setRecvId(uid);
        messageInfo.setSendId(session.getUserId());
        messageInfo.setContent(candidate);

        PrivateChat<PrivateMessageVO> sendMessage = new PrivateChat<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminalType()));
        sendMessage.setReceiverId(uid);
        sendMessage.setSendToSelfOtherTerminals(false);
        sendMessage.setReturnResult(false);
        Integer terminal = getTerminalType(uid, webrtcSession);
        sendMessage.setReceiverTerminals(Collections.singletonList(terminal));
        sendMessage.setContent(messageInfo);
        client.sendPrivateMessage(sendMessage);
    }

    @Override
    public List<ICEServer> getIceServers() {
        return iceServerConfig.getIceServers();
    }

    private WebrtcSession getWebrtcSession(Long userId, Long uid) {
        String key = getSessionKey(userId, uid);
        WebrtcSession webrtcSession = distributeCacheService.getObject(key, WebrtcSession.class);
        if (webrtcSession == null) {
            throw new BJException("视频通话已结束");
        }
        return webrtcSession;
    }

    private void removeWebrtcSession(Long userId, Long uid) {
        String key = getSessionKey(userId, uid);
        distributeCacheService.delete(key);
    }

    private String getSessionKey(Long id1, Long id2) {
        Long minId = id1 > id2 ? id2 : id1;
        Long maxId = id1 > id2 ? id1 : id2;
        return String.join(Constants.REDIS_KEY_SPLIT, Constants.WEBRTC_SESSION, minId.toString(), maxId.toString());
    }

    private Integer getTerminalType(Long uid, WebrtcSession webrtcSession) {
        if (uid.equals(webrtcSession.getCallerId())) {
            return webrtcSession.getCallerTerminal();
        }
        return webrtcSession.getAcceptorTerminal();
    }


}
