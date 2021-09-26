package org.zchzh.zrpcstarter.register;

import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zengchzh
 * @date 2021/9/26
 */
public class ServiceCache {

    private static final Map<String, List<ServiceObject>> SERVICE_CACHE = new ConcurrentHashMap<>();

    public static List<ServiceObject> getCache(String serviceName) {
        return SERVICE_CACHE.get(serviceName) == null ? new CopyOnWriteArrayList<>() : SERVICE_CACHE.get(serviceName);
    }

    public static void putCache(String serviceName, List<ServiceObject> serviceObjectList) {
        SERVICE_CACHE.put(serviceName, serviceObjectList);
    }
}
