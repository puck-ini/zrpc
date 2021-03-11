package com.zchzh.zrpc.server.register;

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
    void register(ServiceObject serviceObject) throws UnknownHostException;

    /**
     * 获取服务对象
     * @param name
     * @return
     */
    ServiceObject getServiceObject(String name);
}
