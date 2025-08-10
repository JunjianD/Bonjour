package com.djj.bj.platform.message.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.message.application.ice.ICEServer;
import com.djj.bj.platform.message.application.service.WebrtcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WebRTC视频通话控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.interfaces.controller
 * @className WebrtcController
 * @date 2025/8/10 21:16
 */
@Tag(name = "webrtc视频通话", description = "提供视频通话等功能")
@RestController
@RequestMapping("/webrtc/private")
public class WebrtcController {
    @Resource
    private WebrtcService webrtcService;

    @Operation(method = "POST", summary = "呼叫视频通话", description = "发起视频通话请求", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))

    })
    @PostMapping("/call")
    public ResponseMessage<String> call(@RequestParam(value = "被呼叫者ID") Long uid, @RequestBody String offer) {
        webrtcService.call(uid, offer);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(method = "POST", summary = "取消呼叫", description = "取消视频通话请求", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/cancel")
    public ResponseMessage<String> cancel(@RequestParam(value = "被呼叫者ID") Long uid) {
        webrtcService.cancel(uid);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(method = "POST", summary = "呼叫失败", description = "处理呼叫失败的情况", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/failed")
    public ResponseMessage<String> failed(@RequestParam(value = "被呼叫者ID") Long uid, @RequestParam(value = "失败原因") String reason) {
        webrtcService.failed(uid, reason);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(method = "POST", summary = "接受视频通话", description = "接受视频通话请求", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/accept")
    public ResponseMessage<String> accept(@RequestParam(value = "被呼叫者ID") Long uid, @RequestBody String answer) {
        webrtcService.accept(uid, answer);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(method = "POST", summary = "拒绝视频通话", description = "拒绝视频通话请求", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/reject")
    public ResponseMessage<String> reject(@RequestParam(value = "被呼叫者ID") Long uid) {
        webrtcService.reject(uid);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(method = "POST", summary = "挂断", description = "挂断视频通话", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/handup")
    public ResponseMessage<String> leave(@RequestParam(value = "被呼叫者ID") Long uid) {
        webrtcService.leave(uid);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @PostMapping("/candidate")
    @Operation(method = "POST", summary = "同步candidate", description = "同步ICE候选者信息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<String> forwardCandidate(@RequestParam Long uid, @RequestBody String candidate) {
        webrtcService.candidate(uid, candidate);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @GetMapping("/iceservers")
    @Operation(method = "GET", summary = "获取iceservers", description = "获取ICE服务器列表", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<List<ICEServer>> iceservers() {
        return ResponseMessageFactory.getSuccessResponseMessage(webrtcService.getIceServers());
    }
}
