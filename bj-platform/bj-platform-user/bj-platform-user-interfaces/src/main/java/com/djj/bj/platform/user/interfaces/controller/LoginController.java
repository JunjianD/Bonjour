package com.djj.bj.platform.user.interfaces.controller;

import com.djj.bj.platform.common.model.dto.LoginDTO;
import com.djj.bj.platform.common.model.dto.RegisterDTO;
import com.djj.bj.platform.common.model.vo.LoginVO;
import com.djj.bj.platform.common.response.ResponseMessage;
import com.djj.bj.platform.common.response.ResponseMessageFactory;
import com.djj.bj.platform.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录认证授权相关接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.interfaces.controller
 * @className LoginController
 * @date 2025/7/23 09:58
 */
@Tag(name = "用户登录和注册", description = "提供用户登录、注册等功能")
@RestController
public class LoginController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录")
    public ResponseMessage<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return ResponseMessageFactory.getSuccessResponseMessage(vo);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册")
    public ResponseMessage<String> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return ResponseMessageFactory.getSuccessResponseMessage("注册成功");
    }
}
