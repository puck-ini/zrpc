package com.zchzh.zrpcstarter.server.register;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengchzh
 * @date 2021/3/11
 *
 * 默认服务注册器
 */
public class DefaultServiceRegister implements ServiceRegister {

    private Map<String, ServiceObject> serviceMap = new HashMap<>();

    protected String protocol;

    protected Integer port;

    @Override
    public void register(ServiceObject serviceObject) throws UnknownHostException {
        if (serviceObject == null){
            throw new IllegalArgumentException("Parameter cannot be empty.");
        }
        this.serviceMap.put(serviceObject.getName(), serviceObject);
    }

    @Override
    public ServiceObject getServiceObject(String name) {
        return this.serviceMap.get(name);
    }
}
