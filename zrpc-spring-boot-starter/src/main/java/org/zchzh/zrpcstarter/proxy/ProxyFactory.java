package org.zchzh.zrpcstarter.proxy;

/**
 * @author zengchzh
 * @date 2021/7/9
 */
public interface ProxyFactory {

    /**
     * 获取代理对象
     * @param clazz
     * @return 代理对象
     */
    Object getProxy(Class<?> clazz);
}
