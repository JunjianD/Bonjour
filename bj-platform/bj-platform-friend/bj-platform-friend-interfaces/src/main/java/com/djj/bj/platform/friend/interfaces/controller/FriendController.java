package com.djj.bj.platform.friend.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.vo.FriendVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.friend.application.service.FriendService;
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
 * 好友关系控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.friend.interfaces.controller
 * @className FriendController
 * @date 2025/8/1 17:23
 */
@Tag(name = "好友", description = "提供好友相关的操作接口")
@RestController
@RequestMapping("/friend")
public class FriendController {
    @Resource
    private FriendService friendService;

    @GetMapping("/list")
    @Operation(summary = "好友列表", description = "获取好友列表", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<List<FriendVO>> findFriends() {
        List<FriendVO> friends = friendService.findFriendByUserId(SessionContext.getUserSession().getUserId());
        return ResponseMessageFactory.getSuccessResponseMessage(friends);
    }

    @PostMapping("/add")
    @Operation(summary = "添加好友", description = "双方建立好友关系", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage addFriend(@NotNull(message = "好友id不可为空") @RequestParam("friendId") Long friendId) {
        friendService.addFriend(friendId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @GetMapping("/find/{friendId}")
    @Operation(summary = "查找好友信息", description = "查找好友信息", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage<FriendVO> findFriend(@NotNull(message = "好友id不可为空") @PathVariable("friendId") Long friendId) {
        return ResponseMessageFactory.getSuccessResponseMessage(friendService.findFriend(friendId));
    }


    @DeleteMapping("/delete/{friendId}")
    @Operation(summary = "删除好友", description = "解除好友关系", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage delFriend(@NotNull(message = "好友id不可为空") @PathVariable("friendId") Long friendId) {
        friendService.delFriend(friendId);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @PutMapping("/update")
    @Operation(summary = "更新好友信息", description = "更新好友头像或昵称", parameters = {
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public ResponseMessage modifyFriend(@Valid @RequestBody FriendVO vo) {
        friendService.update(vo);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }
}
