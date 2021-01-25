package io.github.jonesun.standaloneserver.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @author jone.sun
 * @date 2021/1/25 10:36
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.redisson.file}")
    String redissonFile;

    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {

        //todo 使用redisson-spring-boot-starter默认配置读取redissonFile不起作用，自己引用使用
        return Redisson.create(
                Config.fromYAML(new ClassPathResource(redissonFile.replace("classpath:", ""))
                        .getInputStream()));
    }
}