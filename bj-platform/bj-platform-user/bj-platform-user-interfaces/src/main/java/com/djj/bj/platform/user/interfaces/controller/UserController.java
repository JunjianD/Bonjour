package com.djj.bj.platform.user.interfaces.controller;

import com.djj.bj.platform.common.model.constants.PlatformConstants;
import com.djj.bj.platform.common.model.vo.OnlineTerminalVO;
import com.djj.bj.platform.common.model.vo.UserVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.common.session.SessionContext;
import com.djj.bj.platform.common.session.UserSession;
import com.djj.bj.platform.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.interfaces.controller
 * @className UserController
 * @date 2025/7/24 16:32
 */
@Tag(name = "用户", description = "提供用户相关的操作接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/terminal/online")
    @Operation(summary = "获取在线终端信息", description = "查询用户的在线终端信息,返回在线的用户id的终端集合")
    @Parameters({
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string")),
    })
    public ResponseMessage<List<OnlineTerminalVO>> getOnlineTerminal(@NotEmpty @RequestParam(value = "userIds") String userIds) {
        return ResponseMessageFactory.getSuccessResponseMessage(userService.getOnlineTerminals(userIds));
    }

    @GetMapping("/self")
    @Operation(summary = "获取当前用户信息", description = "查询当前登录用户的信息")
    @Parameters({
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string")),
    })
    public ResponseMessage<UserVO> findSelfInfo() {
        UserSession session = SessionContext.getUserSession();
        UserVO userVO = userService.findUserById(session.getUserId(), false);
        return ResponseMessageFactory.getSuccessResponseMessage(userVO);
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "查找用户", description = "根据id查找用户")
    @Parameters({
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string")),
    })
    public ResponseMessage<UserVO> findById(@NotNull @PathVariable("id") Long id) {
        return ResponseMessageFactory.getSuccessResponseMessage(userService.findUserById(id, true));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户信息", description = "修改用户信息，仅允许修改登录用户信息")
    @Parameters({
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string")),
    })
    public ResponseMessage<String> update(@Valid @RequestBody UserVO vo) {
        userService.update(vo);
        return ResponseMessageFactory.getSuccessResponseMessage();
    }

    @GetMapping("/findByName")
    @Operation(summary = "查找用户", description = "根据用户名或昵称查找用户")
    @Parameters({
            @Parameter(name = PlatformConstants.ACCESS_TOKEN, description = "访问令牌", in = ParameterIn.HEADER, schema = @Schema(type = "string")),
    })
    public ResponseMessage<List<UserVO>> findByName(@RequestParam("name") String name) {
        return ResponseMessageFactory.getSuccessResponseMessage(userService.findUserByName(name));
    }
}