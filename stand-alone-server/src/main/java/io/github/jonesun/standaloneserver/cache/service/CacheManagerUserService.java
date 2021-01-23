package io.github.jonesun.standaloneserver.cache.service;

import io.github.jonesun.standaloneserver.cache.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * 使用CacheManager方式
 *
 * @author jone.sun
 * @date 2021/1/23 9:55
 */
@Service("cacheManagerUserService")
public class CacheManagerUserService implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheManager cacheManager;

    @Override
    public User saveUser(User user) {
        getUserCache().put(user.getId(), user);
        logger.info("保存用户: {}", user);
        return user;
    }

    @Override
    public User findUser(Long userId) {
        return getUserCache().get(userId, User.class);
    }

    @Override
    public User updateUser(User user) {
        getUserCache().put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteById(Long userId) {
        getUserCache().evict(userId);
        return null;
    }

    @Override
    public void clear() {
        getUserCache().clear();
    }

    private Cache getUserCache() {
        return cacheManager.getCache("user");
    }
}
