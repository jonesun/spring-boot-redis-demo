package io.github.jonesun.standaloneserver.pubsub;

import io.github.jonesun.standaloneserver.pubsub.pattern1.MessagePublisher;
import io.github.jonesun.standaloneserver.pubsub.pattern1.RedisMessageSubscriber;
import io.github.jonesun.standaloneserver.pubsub.pattern2.Receiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author jone.sun
 * @date 2021/1/22 13:51
 */
@DisplayName("发布/订阅测试")
@SpringBootTest
class MessagePublisherTest {

    @Autowired
    MessagePublisher messagePublisher;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @DisplayName("方式一")
    @Test
    void publish() {
        String message = "Message " + UUID.randomUUID();
        messagePublisher.publish(message);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(RedisMessageSubscriber.messageList.contains(message));
    }

    @Autowired
    Receiver receiver;

    @DisplayName("方式二")
    @Test
    void convertAndSend() {
        redisTemplate.convertAndSend("chat", "Hello from Redis!");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(receiver.getCount() > 0);
    }
}