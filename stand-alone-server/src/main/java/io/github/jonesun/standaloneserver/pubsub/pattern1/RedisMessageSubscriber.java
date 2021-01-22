package io.github.jonesun.standaloneserver.pubsub.pattern1;

import io.github.jonesun.standaloneserver.StandAloneServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅消息
 *
 * @author jone.sun
 * @date 2021/1/22 13:43
 */
@Service
public class RedisMessageSubscriber implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static List<String> messageList = new ArrayList<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        messageList.add(message.toString());
        logger.info("Message received: {}", message.toString());
    }
}
