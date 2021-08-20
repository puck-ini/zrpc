package org.zchzh.zrpcstarter.model;


import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/4/17
 *
 * 用于保存提供者返回的结果
 */
@Slf4j
public class ResponseHolder {

    private static final Map<String, Promise<ZRpcResponse>> RESULT_MAP = new ConcurrentHashMap<>();

    public static Promise<ZRpcResponse> pop(String key) {
        return RESULT_MAP.remove(key);
    }

    public static void put(String key, Promise<ZRpcResponse> promise) {
        RESULT_MAP.putIfAbsent(key, promise);
    }

    public static Promise<ZRpcResponse> remove(String key) {
        return RESULT_MAP.remove(key);
    }
}
