package io.github.jonesun.standaloneserver.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

/**
 * 使用cache注解方式
 *
 * @author jone.sun
 * @date 2021/1/22 16:46
 */
@Service
@CacheConfig(cacheNames = "user")
public class CacheAnnotationUserService implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Cacheable[] cacheable() default {}; //声明多个@Cacheable
     * CachePut[] put() default {};        //声明多个@CachePut
     * CacheEvict[] evict() default {};    //声明多个@CacheEvict
     * 插入用户
     */
    @Caching(put = {@CachePut(key = "#user.id")})
    @Override
    public User saveUser(User user) {
        logger.info("插入用户: {}", user.getUsername());
        return user;
    }

    /**
     * --@Cacheable注解会先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存
     * 命名空间:@Cacheable的value会替换@CacheConfig的cacheNames(两者必须有一个)
     * --key是[命名空间]::[@Cacheable的key或者KeyGenerator生成的key](@Cacheable的key优先级高,KeyGenerator不配置走默认KeyGenerator SimpleKey [])
     * 使用 sync = true保证只有一个线程访问数据库，避免缓存击穿 ，注意sync = true不能与unless="#result == null"一起使用
     */
    @Cacheable(key = "#userId", sync = true)
    @Override
    public User findUser(Long userId) {
        logger.info("查找用户: {}", userId);
        return null;
    }

    /**
     * --@CachePut注解的作用 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存，
     * 和 @Cacheable 不同的是，它每次都会触发真实方法的调用
     * 简单来说就是用户更新缓存数据。但需要注意的是该注解的value 和 key 必须与要更新的缓存相同，也就是与@Cacheable 相同
     * 默认先执行数据库更新再执行缓存更新
     * 注意返回值必须是要修改后的数据
     */
    @Override
    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        logger.info("更新用户：{}", user.getId());
        return user;
    }

    @Override
    @CachePut(key = "#userId")
    public User deleteById(Long userId) {
        logger.info("更新用户：{}", userId);
        return null;
    }

    /**
     * --@CachEvict 的作用 主要针对方法配置，能够根据一定的条件对缓存进行清空
     * 触发缓存清除
     * 默认先执行数据库删除再执行缓存删除
     */
    @Override
    @CacheEvict(allEntries = true)
    public void clear() {
        logger.info("清除所有");
    }

}
