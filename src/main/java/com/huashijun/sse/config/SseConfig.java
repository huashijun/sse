package com.huashijun.sse.config;

import com.huashijun.sse.util.SseUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huashijun
 */
@Configuration
@ConditionalOnClass(SseUtil.class)
public class SseConfig {

    @Bean
    public SseUtil sseUtil(){
        return new SseUtil();
    }
}