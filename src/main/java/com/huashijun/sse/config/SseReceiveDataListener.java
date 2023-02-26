package com.huashijun.sse.config;

import com.huashijun.sse.util.SseUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author huashijun
 * 消息处理器
 */
@Slf4j
public class SseReceiveDataListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(SseUtil.class);

    private SseUtil sseUtil;
    public SseReceiveDataListener(SseUtil sseUtil){
        this.sseUtil = sseUtil;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("sse消息内容：{} " + message.toString());
        sseUtil.broadcast(message.toString());
    }
}