package org.zchzh.zrpcstarter.proxy;

import org.zchzh.zrpcstarter.register.Register;

/**
 * @author zengchzh
 * @date 2021/7/9
 *
 * 代理
 */
public interface InvokeProxy {

    /**
     * 获取代理对象
     * @param clazz
     * @return 代理对象
     */
    Object getProxy(Class<?> clazz);

    /**
     * 设置服务发现对象
     * @param register 服务发现对象
     */
    void setDiscovery(Register register);
}
