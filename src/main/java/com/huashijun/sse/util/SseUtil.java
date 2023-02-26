package com.huashijun.sse.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author huashijun
 */
@Slf4j
public class SseUtil {
    private static final Logger log = LoggerFactory.getLogger(SseUtil.class);
    private Map<String,SseEmitter> sseEmitterMap = new ConcurrentHashMap<>(2);
    public SseUtil(){}

    /**
     * 注册连接
     */
    public SseEmitter register(String userId,Long timeout) {
        //超时时间设置
        SseEmitter sseEmitter = new SseEmitter(timeout);
        sseEmitterMap.put(userId,sseEmitter);
        //注册回调
        sseEmitter.onCompletion(completionCallBack(userId));
        sseEmitter.onError(errorCallBack(userId));
        sseEmitter.onTimeout(timeoutCallBack(userId));
        log.info("创建新的sse连接，当前用户：{}", userId);
        return sseEmitter;
    }

    /**
     * 单个用户推送数据
     */
    public void push(String userId, String content) {
        if (sseEmitterMap.containsKey(userId)) {
            try {
                sseEmitterMap.get(userId).send(content);
            } catch (IOException e) {
                log.error("用户[{}]推送异常:{}", userId, e.getMessage());
                removeSse(userId);
            }
        }
    }

    /**
     * 广播推送数据
     */
    public void broadcast(String content) {
        sseEmitterMap.forEach((k, v) -> {
            try {
                v.send(content);
            } catch (IOException e) {
                log.error("用户[{}]推送异常:{}", k, e.getMessage());
                removeSse(k);
            }
        });
    }

    /**
     * 移出用户连接
     */
    public void remove(String userId) {
        removeSse(userId);
    }

    /**
     * 结束连接回调
     */
    private Runnable completionCallBack(String userId) {
        return () -> {
            log.info("连接结束：{}", userId);
            removeSse(userId);
        };
    }

    /**
     * 超时回调
     */
    private Runnable timeoutCallBack(String userId) {
        return () -> {
            log.info("连接超时：{}", userId);
            removeSse(userId);
        };
    }

    /**
     * 异常回调
     */
    private Consumer<Throwable> errorCallBack(String userId) {
        return throwable -> {
            log.info("连接异常：{}", userId);
            removeSse(userId);
        };
    }

    /**
     * 移除用户连接
     */
    private void removeSse(String userId) {
        sseEmitterMap.remove(userId);
        log.info("移除连接：{}", userId);
    }
}