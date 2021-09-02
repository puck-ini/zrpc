package org.zchzh.zrpcstarter.model;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/4/17
 *
 * 用于保存提供者返回的结果
 */
@Slf4j
public class ResponseHolder {

    private static final Map<String, CompletableFuture<ZRpcResponse>> RESULT_MAP = new ConcurrentHashMap<>();

    public static CompletableFuture<ZRpcResponse> pop(String key) {
        return RESULT_MAP.remove(key);
    }

    public static void put(String key, CompletableFuture<ZRpcResponse> future) {
        RESULT_MAP.putIfAbsent(key, future);
    }

    public static CompletableFuture<ZRpcResponse> remove(String key) {
        return RESULT_MAP.remove(key);
    }
}
