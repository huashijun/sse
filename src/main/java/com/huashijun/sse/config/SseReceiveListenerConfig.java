package com.huashijun.sse.config;

import com.huashijun.sse.util.SseUtil;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author huashijun
 */
@Configuration
public class SseReceiveListenerConfig extends CachingConfigurerSupport {

    /**
     * 消息监听容器
     */
    @Bean
    RedisMessageListenerContainer sseContainer(LettuceConnectionFactory factory,SseUtil sseUtil){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        //订阅一个通道,该处的通道名是发布消息时的名称
        container.addMessageListener(sseMessageListenerAdapter(sseUtil),new PatternTopic("huashijun_sse"));
        return container;
    }

    /**
     * 消息监听适配器，绑定消息处理器
     */
    @Bean
    MessageListenerAdapter sseMessageListenerAdapter(SseUtil sseUtil){
        return new MessageListenerAdapter(new SseReceiveDataListener(sseUtil));
    }
}