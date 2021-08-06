package org.zchzh.zrpcstarter.register;

import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zengchzh
 * @date 2021/8/6
 */
@Slf4j
public abstract class AbstractRegister implements Register {

    private static final Map<String, List<ServiceObject>> SERVICE_CACHE = new ConcurrentHashMap<>();

    protected List<ServiceObject> getCache(String serviceName) {
        return SERVICE_CACHE.get(serviceName) == null ? new CopyOnWriteArrayList<>() : SERVICE_CACHE.get(serviceName);
    }

    protected void putCache(String serviceName, List<ServiceObject> serviceObjectList) {
        SERVICE_CACHE.put(serviceName, serviceObjectList);
    }
}
