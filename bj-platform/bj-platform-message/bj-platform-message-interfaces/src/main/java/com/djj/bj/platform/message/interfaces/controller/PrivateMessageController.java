package com.djj.bj.platform.message.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.PrivateMessageDTO;
import com.djj.bj.platform.common.model.vo.PrivateMessageVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.message.application.service.PrivateMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 私聊消息控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.interfaces.controller
 * @className PrivateMessageController
 * @date 2025/8/5 15:29
 */
@Tag(name = "私聊消息", description = "提供私聊消息的发送、接收等功能")
@RestController
@RequestMapping("/message/private")
public class PrivateMessageController {
    @Resource
    private PrivateMessageService privateMessageService;

    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送私聊消息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<Long> sendMessage(@Valid @RequestBody PrivateMessageDTO dto) {
        return ResponseMessageFactory.getSuccessResponseMessage(privateMessageService.sendMessage(dto));
    }

    @PostMapping("/pullUnreadMessage")
    @Operation(summary = "拉取未读消息", description = "拉取未读消息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<String> pullUnreadMessage() {
        privateMessageService.pullUnreadMessage();
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @GetMapping("/loadMessage")
    @Operation(summary = "拉取消息", description = "拉取消息,一次最多拉取100条", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<List<PrivateMessageVO>> loadMessage(@RequestParam(value = "minId") Long minId) {
        return ResponseMessageFactory.getSuccessResponseMessage(privateMessageService.loadMessage(minId));
    }

    @GetMapping("/history")
    @Operation(summary = "查询聊天记录", description = "查询聊天记录", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<List<PrivateMessageVO>> recallMessage(@NotNull(message = "好友id不能为空") @RequestParam(value = "friendId") Long friendId,
                                                                 @NotNull(message = "页码不能为空") @RequestParam(value = "page") Long page,
                                                                 @NotNull(message = "size不能为空") @RequestParam(value = "size") Long size) {
        return ResponseMessageFactory.getSuccessResponseMessage(privateMessageService.getHistoryMessage(friendId, page, size));
    }

    @PutMapping("/readed")
    @Operation(summary = "消息已读", description = "将会话中接收的消息状态置为已读", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<String> readedMessage(@RequestParam(value = "friendId") Long friendId) {
        privateMessageService.readedMessage(friendId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @DeleteMapping("/recall/{id}")
    @Operation(summary = "撤回消息", description = "撤回私聊消息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<Long> withdrawMessage(@NotNull(message = "消息id不能为空") @PathVariable("id") Long id) {
        privateMessageService.withdrawMessage(id);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }
}
