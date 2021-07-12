package org.zchzh.zrpcstarter.cluster;



import org.zchzh.zrpcstarter.annotation.JdkSPI;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/7/11
 */
public class LoadBalanceFactory {

    private static final Map<String, LoadBalance> MAP = new ConcurrentHashMap<>();

    public static LoadBalance getInstance(String name) {
        return MAP.computeIfAbsent(name, i -> get(name));
    }

    private static LoadBalance get(String name) {
        ServiceLoader<LoadBalance> loader = ServiceLoader.load(LoadBalance.class);
        for (LoadBalance loadBalance : loader) {
            JdkSPI jdkSPI = loadBalance.getClass().getAnnotation(JdkSPI.class);
            if (Objects.isNull(jdkSPI)) {
                throw new IllegalArgumentException("load balance name can not be empty");
            }
            if (Objects.equals(name, jdkSPI.value())) {
                return loadBalance;
            }
        }
        throw new RuntimeException("invalid load balance config");
    }

}
