package com.djj.bj.platform.user.application.service;

import com.djj.bj.platform.common.model.dto.LoginDTO;
import com.djj.bj.platform.common.model.dto.RegisterDTO;
import com.djj.bj.platform.common.model.vo.LoginVO;

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
}
