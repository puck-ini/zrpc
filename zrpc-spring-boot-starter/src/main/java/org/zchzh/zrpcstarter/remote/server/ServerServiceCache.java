package org.zchzh.zrpcstarter.remote.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/7/9
 */
public class ServerServiceCache {

    private static final Map<String, Object> SERVICE_CACHE = new ConcurrentHashMap<>();


    public static Object get(String key) {
        return SERVICE_CACHE.get(key);
    }


    public static void put(String key, Object object) {
        SERVICE_CACHE.put(key, object);
    }
}
