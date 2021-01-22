package io.github.jonesun.standaloneserver.pubsub.pattern1;

/**
 * 消息发布者
 *
 * @author jone.sun
 * @date 2021/1/22 13:41
 */
public interface MessagePublisher {

    void publish(String message);

}
