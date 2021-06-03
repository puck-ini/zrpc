package org.zchzh.zrpcstarter.proxy;

import org.zchzh.zrpcstarter.cache.ClientCache;
import org.zchzh.zrpcstarter.client.Client;
import org.zchzh.zrpcstarter.discovery.ServiceDiscover;
import org.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import org.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import org.zchzh.zrpcstarter.protocol.service.ServiceObject;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;


/**
 * @author zengchzh
 * @date 2021/3/11
 *
 * 动态代理
 * 通过对服务接口的动态代理，封装消费者发起的请求，同时获取响应
 */
@Slf4j
@Data
public class ClientProxyFactory {

    private ServiceDiscover serviceDiscover;

    private Map<Class<?>, Object> objectCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) this.objectCache.computeIfAbsent(clazz,
                cls -> Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, new ClientInvocationHandler(cls)));
    }


    private class ClientInvocationHandler implements InvocationHandler{

        private Class<?> clazz;

        private Random random = new Random();

        public ClientInvocationHandler(Class<?> clazz) {
            super();
            this.clazz = clazz;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("toString".equals(method.getName())) {
                return proxy.getClass().toString();
            }

            if ("hashCode".equals(method.getName())) {
                return 0;
            }

            // 获取服务信息
            String serviceName = this.clazz.getName();
            List<ServiceObject> serviceList = serviceDiscover.getService(serviceName);

            if (CollectionUtils.isEmpty(serviceList)) {
                log.error("ServiceNoFoundException");
                return null;
            }

            // 随机选择一个服务提供者（软负载均衡）
            ServiceObject service = serviceList.get(random.nextInt(serviceList.size()));

            // 构造request对象
            ZRpcRequest request = new ZRpcRequest();
            request.setClassName(serviceName);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setVersion("version");
            request.setRequestId(UUID.randomUUID().toString());

            Client client = ClientCache.MAP.get(ClientCache.MAP.makeKey(service));
            Promise<ZRpcResponse> promise = client.getHandler().send(request);
            long start = System.currentTimeMillis();
            ZRpcResponse response = promise.get();
            log.info("get response cost >>>>>>>>>>>>>>>> : " + (System.currentTimeMillis() - start));
            if (response.getError() != null) {
                log.error("clientProxyFactory getError : " + response.getError());
            }
            return response.getResult();
        }
    }


}
