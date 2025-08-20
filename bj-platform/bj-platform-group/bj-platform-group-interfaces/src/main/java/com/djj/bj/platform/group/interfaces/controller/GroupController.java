package com.djj.bj.platform.group.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.vo.GroupInviteVO;
import com.djj.bj.platform.common.model.vo.GroupMemberVO;
import com.djj.bj.platform.common.model.vo.GroupVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.group.application.service.GroupService;
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
 * 群组控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.interfaces.controller
 * @className GroupController
 * @date 2025/8/4 11:52
 */
@Tag(name = "群聊", description = "提供群组的创建、修改、删除等功能")
@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    private GroupService groupService;

    @Operation(summary = "创建群聊", description = "创建群聊", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/create")
    public ResponseMessage<GroupVO> createGroup(@Valid @RequestBody GroupVO vo) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupService.createGroup(vo));
    }

    @Operation(summary = "修改群聊信息", description = "修改群聊信息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PutMapping("/modify")
    public ResponseMessage<GroupVO> modifyGroup(@Valid @RequestBody GroupVO vo) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupService.modifyGroup(vo));
    }

    @Operation(summary = "解散群聊", description = "解散群聊", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @DeleteMapping("/delete/{groupId}")
    public ResponseMessage deleteGroup(@NotNull(message = "群聊id不能为空") @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(summary = "查询群聊", description = "查询单个群聊信息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @GetMapping("/find/{groupId}")
    public ResponseMessage<GroupVO> findGroup(@NotNull(message = "群聊id不能为空") @PathVariable("groupId") Long groupId) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupService.findById(groupId));
    }

    @Operation(summary = "查询群聊列表", description = "查询群聊列表", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @GetMapping("/list")
    public ResponseMessage<List<GroupVO>> findGroups() {
        return ResponseMessageFactory.getSuccessResponseMessage(groupService.findGroups());
    }

    @Operation(summary = "邀请进群", description = "邀请好友进群", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/invite")
    public ResponseMessage invite(@Valid @RequestBody GroupInviteVO vo) {
        groupService.invite(vo);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(summary = "查询群聊成员", description = "查询群聊成员", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @GetMapping("/members/{groupId}")
    public ResponseMessage<List<GroupMemberVO>> findGroupMembers(@NotNull(message = "群聊id不能为空") @PathVariable("groupId") Long groupId) {
        return ResponseMessageFactory.getSuccessResponseMessage(groupService.findGroupMembers(groupId));
    }

    @Operation(summary = "退出群聊", description = "退出群聊", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @DeleteMapping("/quit/{groupId}")
    public ResponseMessage quitGroup(@NotNull(message = "群聊id不能为空") @PathVariable("groupId") Long groupId) {
        groupService.quitGroup(groupId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @Operation(summary = "踢出群聊", description = "将用户踢出群聊", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @DeleteMapping("/kick/{groupId}")
    public ResponseMessage kickGroup(@NotNull(message = "群聊id不能为空") @PathVariable("groupId") Long groupId,
                                     @NotNull(message = "用户id不能为空") @RequestParam(value = "userId") Long userId) {
        groupService.kickGroup(groupId, userId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }
}
