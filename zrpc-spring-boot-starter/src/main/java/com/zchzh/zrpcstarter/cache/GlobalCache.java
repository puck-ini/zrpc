package com.zchzh.zrpcstarter.cache;

import com.zchzh.zrpcstarter.client.NettyClientHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zengchzh
 * @date 2021/4/15
 */
public enum  GlobalCache {
    /**
     * 全局缓存实例
     */
    INSTANCE;

    private static final Map<String, NettyClientHandler> handlerMap = new ConcurrentHashMap<>();

    public NettyClientHandler get(String key) {
        return handlerMap.get(key);
    }

    public void put(String key, NettyClientHandler handler) {
        handlerMap.putIfAbsent(key, handler);
    }

    public void remove(String key) {
        handlerMap.remove(key);
    }
}
