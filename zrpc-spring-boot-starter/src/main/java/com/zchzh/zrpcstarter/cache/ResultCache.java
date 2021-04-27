package com.zchzh.zrpcstarter.cache;


import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
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
    MAP;

    private static final Object LOCK = new Object();

    private static final Map<String, Promise<ZRpcResponse>> resultMap = new ConcurrentHashMap<>();

    public Promise<ZRpcResponse> get(String key) throws InterruptedException {
        log.info("resultMap + " + resultMap.entrySet().size() + ":" + new Date());
        if (resultMap.remove(key) == null) {
            await();
        }
        Promise<ZRpcResponse> promise = resultMap.remove(key);
        promise.await();
        if (promise.isSuccess()){
            return promise;
        }
        return null;
    }

    public void put(String key, Promise<ZRpcResponse> handler) {
        resultMap.putIfAbsent(key, handler);
    }

    public void await() throws InterruptedException {
        LOCK.wait();
    }

    public void notifyLock() {
        LOCK.notify();
    }
}
