package com.zchzh.zrpcstarter.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.zchzh.zrpcstarter.protocol.service.ServiceObject;

import java.net.UnknownHostException;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public interface ServiceRegister {

    /**
     * 注册服务对象
     * @param serviceObject
     */
    void register(ServiceObject serviceObject) throws UnknownHostException, NacosException;

    /**
     * 获取服务对象
     * @param name
     * @return
     */
    ServiceObject getServiceObject(String name);
}
