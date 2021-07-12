package org.zchzh.zrpcstarter.proxy;

import io.netty.util.concurrent.Promise;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.cluster.LoadBalanceFactory;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.client.ClientServiceCache;


import javax.annotation.Resource;
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
public class JdkProxyFactory implements ProxyFactory {


    @Resource
    private Register register;

    @Override
    public Object getProxy(Class<?> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new ClientInvocationHandler(clazz));
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private final Class<?> clazz;

        public ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String serviceName = clazz.getName();
            List<ServiceObject> serviceObjectList = register.getAll(serviceName);
            if (CollectionUtils.isEmpty(serviceObjectList)) {
                throw new RuntimeException("can not find service with name : " + serviceName);
            }
            String loadBalanceName = serviceObjectList.get(0).getMeta().get(Constants.LOAD_BALANCE);
            LoadBalance loadBalance = LoadBalanceFactory.getInstance(loadBalanceName);
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
