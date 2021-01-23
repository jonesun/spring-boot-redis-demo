package io.github.jonesun.standaloneserver.cache.service;

import io.github.jonesun.standaloneserver.cache.User;

/**
 * @author jone.sun
 * @date 2021/1/22 16:45
 */
public interface UserService {

    /**
     * 插入用户
     *
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * 获取用户
     *
     * @return
     */
    User findUser(Long userId);

    /**
     * 更新用户信息
     */
    User updateUser(User user);

    /**
     * 删除数据
     * @param userId
     * @return
     */
    User deleteById(Long userId);

    /**
     * 清除缓存的用户信息
     */
    void clear();

}
