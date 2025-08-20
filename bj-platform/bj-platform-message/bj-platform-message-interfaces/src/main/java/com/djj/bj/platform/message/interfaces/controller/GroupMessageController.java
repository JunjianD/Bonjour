package com.djj.bj.platform.message.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.dto.GroupMessageDTO;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.message.application.service.GroupMessageService;
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
 * 群聊消息控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.interfaces.controller
 * @className GroupMessageController
 * @date 2025/8/7 11:45
 */
@Tag(name = "群聊消息", description = "提供群聊消息的发送、接收等功能")
@RestController
@RequestMapping("/message/group")
public class GroupMessageController {
    @Resource
    private GroupMessageService groupMessageService;

    @PostMapping("/send")
    @Operation(summary = "发送群聊消息", description = "发送群聊消息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<Long> sendMessage(@Valid @RequestBody GroupMessageDTO dto) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupMessageService.sendMessage(dto));
    }

    @PostMapping("/pullUnreadMessage")
    @Operation(summary = "拉取未读消息", description = "拉取未读消息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage pullUnreadMessage() {
        groupMessageService.pullUnreadMessage();
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @GetMapping("/loadMessage")
    @Operation(summary = "拉取消息", description = "拉取消息,一次最多拉取100条", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<List<GroupMessageVO>> loadMessage(@RequestParam(value = "minId") Long minId) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupMessageService.loadMessage(minId));
    }

    @GetMapping("/history")
    @Operation(summary = "查询聊天记录", description = "查询聊天记录", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<List<GroupMessageVO>> recallMessage(@NotNull(message = "群聊id不能为空") @RequestParam(value = "groupId") Long groupId,
                                                               @NotNull(message = "页码不能为空") @RequestParam(value = "page") Long page,
                                                               @NotNull(message = "size不能为空") @RequestParam(value = "size") Long size) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupMessageService.findHistoryMessage(groupId, page, size));
    }

    @PutMapping("/readed")
    @Operation(summary = "消息已读", description = "将群聊中的消息状态置为已读", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage readedMessage(@RequestParam(value = "groupId") Long groupId) {
        groupMessageService.readedMessage(groupId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @DeleteMapping("/recall/{id}")
    @Operation(summary = "撤回消息", description = "撤回群聊消息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<Long> recallMessage(@NotNull(message = "消息id不能为空") @PathVariable("id") Long id) {
        groupMessageService.withdrawMessage(id);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }
}
