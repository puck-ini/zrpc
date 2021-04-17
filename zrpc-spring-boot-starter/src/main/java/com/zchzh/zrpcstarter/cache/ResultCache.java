package com.zchzh.zrpcstarter.cache;


import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zengchzh
 * @date 2021/4/17
 */
@Slf4j
public enum ResultCache {

    /**
     * 返回结果缓存实例
     */
    INSTANCE;

    private static final Map<String, Promise<ZRpcResponse>> resultMap = new ConcurrentHashMap<>();

    public Promise<ZRpcResponse> get(String key) {
        log.info("resultMap + " + resultMap.entrySet().size());
        if (resultMap.remove(key) == null){
            LockSupport.park(this);
        }
        return resultMap.remove(key);
    }

    public void put(String key, Promise<ZRpcResponse> handler) {
        resultMap.putIfAbsent(key, handler);
        LockSupport.unpark(Thread.currentThread());
    }
}
