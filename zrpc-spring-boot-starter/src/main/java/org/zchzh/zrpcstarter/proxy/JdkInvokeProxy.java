package org.zchzh.zrpcstarter.proxy;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.google.auto.service.AutoService;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.client.ClientServiceCache;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zengchzh
 * @date 2021/7/10
 */

@Slf4j
@AutoService(InvokeProxy.class)
@JdkSPI(Constants.JDK)
public class JdkInvokeProxy implements InvokeProxy {


    /**
     * service 缓存
     */
    private static final TimedCache<String, List<ServiceObject>> SERVICE_CACHE
            = CacheUtil.newTimedCache(10000);

    static {
        SERVICE_CACHE.schedulePrune(10000);
    }

    private Register register;

    @Override
    public Object getProxy(Class<?> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new ClientInvocationHandler(clazz));
    }

    @Override
    public void setDiscovery(Register register) {
        this.register = register;
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private final Class<?> clazz;

        public ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String serviceName = clazz.getName();
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
            ServiceObject serviceObject = loadBalance.get(serviceObjectList);
            ZRpcRequest request = ZRpcRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .className(serviceName)
                    .methodName(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .parameters(args)
                    .build();
            Promise<ZRpcResponse> promise = ClientServiceCache
                    .getClient(serviceObject.getIp(), serviceObject.getPort())
                    .invoke(request);
            ZRpcResponse response = promise.get();
            Object result = response.getResult();
            if (Objects.isNull(result)) {
                throw new RuntimeException(response.getError());
            }
            return result;
        }
    }
}
