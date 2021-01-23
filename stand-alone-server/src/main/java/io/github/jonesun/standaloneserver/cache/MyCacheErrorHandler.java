package io.github.jonesun.standaloneserver.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

/**
 * 使用cache方式时，如果发送与redis交互错误时的回调
 * @author jone.sun
 * @date 2020-08-25 10:27
 */
public class MyCacheErrorHandler implements org.springframework.cache.interceptor.CacheErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        LOGGER.error("cache get error, cacheName:{}, key:{}, msg:", cache.getName(), key, exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        LOGGER.error("cache put error, cacheName:{}, key:{}, msg:", cache.getName(), key, exception);

    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        LOGGER.error("cache evict error, cacheName:{}, key:{}, msg:", cache.getName(), key, exception);

    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        LOGGER.error("cache clear error, cacheName:{}, msg:", cache.getName(), exception);
    }
}