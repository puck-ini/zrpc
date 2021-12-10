package org.zchzh.zrpcstarter.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.exception.CommonException;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.client.ClientHolder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @author zengchzh
 * @date 2021/8/5
 */
@Slf4j
public abstract class AbstractInvocationHandler {

    protected final Class<?> clazz;

    private final Register discovery;

    private final LoadBalance loadBalance;

    public AbstractInvocationHandler(Class<?> clazz, Register discovery, LoadBalance loadBalance) {
        this.clazz = clazz;
        this.discovery = discovery;
        this.loadBalance = loadBalance;
    }

    protected Object handler(Object o, Method method, Object[] args) {
        String serviceName = clazz.getName();
        ZRpcRequest request = ZRpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .className(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        ServiceObject so = getServiceObject(serviceName);
        return ClientHolder.get(so).invoke(request).getResult();
    }

    private ServiceObject getServiceObject(String serviceName) {
        List<ServiceObject> serviceObjectList = discovery.getAll(serviceName);
        if (CollectionUtils.isEmpty(serviceObjectList)) {
            throw new CommonException("can not find service with name : " + serviceName);
        }
        return loadBalance.get(serviceObjectList);
    }
}
