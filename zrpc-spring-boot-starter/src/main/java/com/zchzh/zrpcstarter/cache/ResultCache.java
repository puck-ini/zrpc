package com.zchzh.zrpcstarter.cache;


import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<String, Promise<ZRpcResponse>> RESULT_MAP = new ConcurrentHashMap<>();

    public Promise<ZRpcResponse> get(String key) throws InterruptedException {
        return RESULT_MAP.get(key);
    }

    public void put(String key, Promise<ZRpcResponse> handler) {
        RESULT_MAP.putIfAbsent(key, handler);
    }

    public Promise<ZRpcResponse> remove(String key) {
        return RESULT_MAP.remove(key);
    }
}
