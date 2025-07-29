package com.djj.bj.platform.user.application.service;

import com.djj.bj.platform.common.model.dto.LoginDTO;
import com.djj.bj.platform.common.model.dto.ModifyPwdDTO;
import com.djj.bj.platform.common.model.dto.RegisterDTO;
import com.djj.bj.platform.common.model.entity.User;
import com.djj.bj.platform.common.model.vo.LoginVO;
import com.djj.bj.platform.common.model.vo.OnlineTerminalVO;
import com.djj.bj.platform.common.model.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.application.service
 * @interfaceName UserService
 * @date 2025/7/22 19:56
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param dto 登录数据传输对象
     * @return 登录信息值对象
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户注册
     *
     * @param dto 注册数据传输对象
     */
    void register(RegisterDTO dto);

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 刷新后的登录信息值对象
     */
    LoginVO refreshToken(String refreshToken);

    /**
     * 修改用户密码
     *
     * @param dto 修改密码数据传输对象
     */
    void modifyPassword(ModifyPwdDTO dto);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findUserByUserName(String username);

    /**
     * 更新用户信息
     *
     * @param vo 用户信息值对象
     */
    void update(UserVO vo);

    /**
     * 根据用户ID查询用户信息和在线状态
     *
     * @param id                  用户ID
     * @param constantsOnlineFlag 是否使用常量在线标志
     * @return 用户信息值对象
     */
    UserVO findUserById(Long id, boolean constantsOnlineFlag);

    /**
     * 根据用户ID获取用户实体对象
     *
     * @param userId 用户ID
     * @return 用户实体对象
     */
    User getUserById(Long userId);

    /**
     * 根据用户昵称查询用户，最多返回20条数据
     *
     * @param name 用户名或昵称
     * @return 用户信息列表
     */
    List<UserVO> findUserByName(String name);

    /**
     * 获取用户在线的终端类型
     *
     * @param userIds 用户ID，多个用逗号分隔
     * @return 在线用户终端信息列表
     */
    List<OnlineTerminalVO> getOnlineTerminals(String userIds);
}
