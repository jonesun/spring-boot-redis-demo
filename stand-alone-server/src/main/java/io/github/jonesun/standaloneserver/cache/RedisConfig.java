package io.github.jonesun.standaloneserver.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.*;

/**
 * @author jone.sun
 * @date 2021/1/22 18:02
 */
@EnableCaching
@Configuration("cache")
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

//    /**
//     * 自定义生成redis-key
//     */
//    @Override
//    public KeyGenerator keyGenerator() {
//        return (o, method, objects) -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append(o.getClass().getName()).append(".");
//            sb.append(method.getName()).append(".");
//            for (Object obj : objects) {
//                sb.append(obj.toString());
//            }
//            return sb.toString();
//        };
//    }


    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate() {
        return configRedisTemplate(Object.class, redisConnectionFactory);
    }

    @Bean
    public RedisTemplate<String, User> userRedisTemplate() {
        return configRedisTemplate(User.class, redisConnectionFactory);
    }


    /**
     * 可以根据自己实际项目需要，定制多个CacheManager，注解的地方可以指定使用哪个CacheManager
     * @param objectRedisTemplate
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> objectRedisTemplate) {
        //如果支持使用objectRedisTemplate，没有用注解或者cacheManager方式的话，则此配置不生效，即key相关的规则，需使用者自己定义
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
//                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "spring-redis".concat(":").concat(cacheName).concat(":"))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(objectRedisTemplate.getStringSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(objectRedisTemplate.getValueSerializer()));

        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("user");
        cacheNames.add("test");


        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("user", cacheConfiguration.entryTtl(Duration.ofSeconds(120)));
        configMap.put("test", cacheConfiguration.entryTtl(Duration.ofSeconds(2)));

        return RedisCacheManager.builder(Objects.requireNonNull(objectRedisTemplate.getConnectionFactory()))
                .cacheDefaults(cacheConfiguration)
                .initialCacheNames(cacheNames)
                .withInitialCacheConfigurations(configMap)
                //在spring事务正常提交时才缓存数据
                .transactionAware()
                .build();
    }

    private <T> RedisTemplate<String, T> configRedisTemplate(Class<T> clazz, RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<T> j2jrs = new Jackson2JsonRedisSerializer<>(clazz);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());

        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        j2jrs.setObjectMapper(om);

//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
//        redisTemplate.setDefaultSerializer(serializer);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
//        redisTemplate.setValueSerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(j2jrs);
        redisTemplate.setHashValueSerializer(j2jrs);
        redisTemplate.afterPropertiesSet();

//        //开启事务支持
//        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        //设置和redis交互报错时的错误处理器，仅在使用cacheAnnotation注解方式时生效
        return new MyCacheErrorHandler();
    }


}
