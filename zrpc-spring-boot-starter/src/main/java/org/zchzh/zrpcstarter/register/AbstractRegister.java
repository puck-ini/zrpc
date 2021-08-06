package org.zchzh.zrpcstarter.register;

import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/8/6
 */
public abstract class AbstractRegister implements Register {

    private static final Map<String, List<ServiceObject>> SERVICE_CACHE = new ConcurrentHashMap<>();

    protected List<ServiceObject> get(String serviceName) {
        return SERVICE_CACHE.get(serviceName) == null ? new ArrayList<>() : SERVICE_CACHE.get(serviceName);
    }

    protected void put(String serviceName, List<ServiceObject> serviceObjectList) {
        SERVICE_CACHE.put(serviceName, serviceObjectList);
    }
}
