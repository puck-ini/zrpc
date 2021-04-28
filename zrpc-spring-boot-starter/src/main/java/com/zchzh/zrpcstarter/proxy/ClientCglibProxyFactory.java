package com.zchzh.zrpcstarter.proxy;

import com.zchzh.zrpcstarter.client.old.NettyClient;
import com.zchzh.zrpcstarter.discovery.ServiceDiscover;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zengchzh
 * @date 2021/3/24
 */

@Deprecated
@Slf4j
@Data
public class ClientCglibProxyFactory {

    private ServiceDiscover serviceDiscover;

    private NettyClient nettyClient;

    private Map<Class<?>, Object> objectCache = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ClientMethodInterceptor());
        T t = (T) objectCache.computeIfAbsent(clazz, cls -> enhancer.create());
        return t;
    }

    private class ClientMethodInterceptor implements MethodInterceptor {

        private Random random = new Random();


        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//            if ("toString".equals(method.getName())) {
//                return methodProxy.getClass().toString();
//            }
//
//            if ("hashCode".equals(method.getName())) {
//                return 0;
//            }
//            // 获取服务信息
//            String serviceName = o.getClass().getName();
//            List<Service> serviceList = serviceDiscover.getService(serviceName);
//
//            if (CollectionUtils.isEmpty(serviceList)) {
//                log.error("ServiceNoFoundException");
//                return null;
//            }
//
//            // 随机选择一个服务提供者（软负载均衡）
//            Service service = serviceList.get(random.nextInt(serviceList.size()));
//
//            // 构造request对象
//            ZRpcRequest request = new ZRpcRequest();
//            request.setClassName(serviceName);
//            request.setMethodName(method.getName());
//            request.setParameterTypes(method.getParameterTypes());
//            request.setParameters(objects);
//            request.setVersion("version");
//            request.setRequestId(UUID.randomUUID().toString());
//
//            // 协议
//            ZRpcResponse response = nettyClient.sendRequest(request,service);
//            log.info("proxy success");
//            if (response.getError() != null) {
//                log.error("clientProxyFactory getError : " + response.getError());
//            }
//            return response.getResult();
            return null;
        }
    }
}
