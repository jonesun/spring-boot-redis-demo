package io.github.jonesun.standaloneserver.cache.service;

import io.github.jonesun.standaloneserver.cache.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 直接使用RedisTemplate的方式
 *
 * @author jone.sun
 * @date 2021/1/23 10:27
 */
@Service("redisTemplateUserService")
public class RedisTemplateUserService implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final String PREFIX_CACHE_REDIS_KEY_USER = "spring-redis:user:";

    @Autowired
    RedisTemplate<String, User> userRedisTemplate;

    @Override
    public User saveUser(User user) {
        userRedisTemplate.opsForValue().set(getRealKeyById(user.getId()), user);
        return null;
    }

    @Override
    public User findUser(Long userId) {
        return userRedisTemplate.opsForValue().get(getRealKeyById(userId));
    }

    @Override
    public User updateUser(User user) {
        userRedisTemplate.opsForValue().set(getRealKeyById(user.getId()), user);
        return null;
    }

    @Override
    public User deleteById(Long userId) {
        Boolean result = userRedisTemplate.delete(getRealKeyById(userId));
        logger.info("删除结果: {}", result);
        return null;
    }

    @Override
    public void clear() {
        Set<String> keys = userRedisTemplate.keys(PREFIX_CACHE_REDIS_KEY_USER + "*");
        if (keys != null) {
            userRedisTemplate.delete(keys);
        }
    }

    /**
     * 获取真实key
     *
     * @param userId
     * @return
     */
    private String getRealKeyById(Long userId) {
        return PREFIX_CACHE_REDIS_KEY_USER + userId;
    }
}
