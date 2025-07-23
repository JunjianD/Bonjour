package com.djj.bj.platform.user.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.djj.bj.platform.common.model.entity.User;

import java.util.List;

/**
 * 领域层用户服务接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.user.domain.service
 * @interfaceName UserDomainService
 * @date 2025/7/22 19:37
 */
public interface UserDomainService extends IService<User> {
    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return User 用户信息
     */
    User getUserByUserName(String userName);

    /**
     * 保存或更新用户信息
     *
     * @param user 用户信息
     */
    void saveOrUpdateUser(User user);

    /**
     * 根据名称模糊查询用户列表
     *
     * @param name 模糊名称
     * @return List<User> 用户列表
     */
    List<User> getUserListByName(String name);
}
