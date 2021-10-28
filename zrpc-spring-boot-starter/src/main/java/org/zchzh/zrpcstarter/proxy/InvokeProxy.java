package org.zchzh.zrpcstarter.proxy;

import org.zchzh.zrpcstarter.constants.Constants;
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
     * @param clazz 被代理类
     * @return 代理对象
     */
    default Object getProxy(Class<?> clazz) {
        return getProxy(clazz, Constants.RANDOM);
    }

    /**
     * 获取代理对象，设置负载均衡
     * @param clazz 被代理类
     * @param loadBalance 负载均衡方式
     * @return 返回代理对象
     */
    Object getProxy(Class<?> clazz, String loadBalance);
}
