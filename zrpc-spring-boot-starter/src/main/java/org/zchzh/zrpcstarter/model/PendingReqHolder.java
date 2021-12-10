package org.zchzh.zrpcstarter.model;


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
public class PendingReqHolder {

    private static final Map<String, PendingRequest> RESULT_MAP = new ConcurrentHashMap<>();

    public static PendingRequest get(String key) {
        return RESULT_MAP.get(key);
    }

    public static PendingRequest pop(String key) {
        return RESULT_MAP.remove(key);
    }

    public static void put(String key, PendingRequest pendingRequest) {
        RESULT_MAP.putIfAbsent(key, pendingRequest);
    }

    public static PendingRequest remove(String key) {
        return RESULT_MAP.remove(key);
    }
}
