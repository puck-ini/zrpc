package org.zchzh.zrpcstarter.proxy;

import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.exception.CommonException;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/7/22
 */
public class InvokeProxyFactory {

    private static final Map<String, InvokeProxy> INVOKE_PROXY_MAP = new ConcurrentHashMap<>();

    public static InvokeProxy getInstance(String name) {
        return INVOKE_PROXY_MAP.computeIfAbsent(name, k -> get(name));
    }

    private static InvokeProxy get(String name) {
        ServiceLoader<InvokeProxy> loader = ServiceLoader.load(InvokeProxy.class);
        for (InvokeProxy invokeProxy : loader) {
            JdkSPI jdkSPI = invokeProxy.getClass().getAnnotation(JdkSPI.class);
            if (Objects.isNull(jdkSPI)) {
                throw new IllegalArgumentException("invoke proxy name can not be empty");
            }
            if (Objects.equals(name, jdkSPI.value())) {
                return invokeProxy;
            }
        }
        throw new CommonException("invalid invoke proxy config");
    }
}
