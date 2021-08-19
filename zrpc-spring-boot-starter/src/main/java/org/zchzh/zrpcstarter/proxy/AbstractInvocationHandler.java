package org.zchzh.zrpcstarter.proxy;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import io.netty.util.concurrent.Promise;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.client.ClientServiceCache;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author zengchzh
 * @date 2021/8/5
 */
public abstract class AbstractInvocationHandler {

    protected final Class<?> clazz;

    /**
     * service 缓存
     */
    private static final TimedCache<String, List<ServiceObject>> SERVICE_CACHE
            = CacheUtil.newTimedCache(10000);

    static {
        SERVICE_CACHE.schedulePrune(10000);
    }

    public AbstractInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected Object handler(Object o, Method method, Object[] args, Register register) throws ExecutionException, InterruptedException {
        String serviceName = clazz.getName();
        ServiceObject serviceObject = getServiceObject(serviceName, register);
        ZRpcRequest request = ZRpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .className(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        Promise<ZRpcResponse> promise = ClientServiceCache
                .getClient(serviceObject.getIp(), serviceObject.getPort(), serviceObject.getMeta().get(Constants.SERIALIZER))
                .invoke(request);
        ZRpcResponse response = promise.get();
        Object result = response.getResult();
        if (Objects.isNull(result)) {
            throw new RuntimeException(response.getError());
        }
        return result;
    }

    private ServiceObject getServiceObject(String serviceName, Register register) {

//            List<ServiceObject> serviceObjectList = SERVICE_CACHE.get(serviceName, false);
//            if (CollectionUtils.isEmpty(serviceObjectList)) {
//                serviceObjectList = register.getAll(serviceName);
//                SERVICE_CACHE.put(serviceName, serviceObjectList, 10000);
//            }

//            long start = System.currentTimeMillis();
//            log.info("start : " + start);
        List<ServiceObject> serviceObjectList = register.getAll(serviceName);
//            log.info("cost : " + (System.currentTimeMillis() - start));

        if (CollectionUtils.isEmpty(serviceObjectList)) {
            throw new RuntimeException("can not find service with name : " + serviceName);
        }
        String loadBalanceName = serviceObjectList.get(0).getMeta().get(Constants.LOAD_BALANCE);
        LoadBalance loadBalance = (LoadBalance) FactoryProducer.INSTANCE.getInstance(Constants.CLUSTER)
                .getInstance(loadBalanceName);
        return loadBalance.get(serviceObjectList);
    }
}
