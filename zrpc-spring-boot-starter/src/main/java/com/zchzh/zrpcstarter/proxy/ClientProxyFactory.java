package com.zchzh.zrpcstarter.proxy;

import com.zchzh.zrpcstarter.client.NettyClient;
import com.zchzh.zrpcstarter.client.discovery.ServiceDiscover;
import com.zchzh.zrpcstarter.model.request.ZRpcRequest;
import com.zchzh.zrpcstarter.model.respones.ZRpcResponse;
import com.zchzh.zrpcstarter.model.service.Service;
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
 */
@Slf4j
public class ClientProxyFactory {

    private ServiceDiscover serviceDiscover;

    private NettyClient nettyClient;

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
            List<Service> serviceList = serviceDiscover.getService(serviceName);

            if (CollectionUtils.isEmpty(serviceList)) {
                log.error("ServiceNoFoundException");
                return null;
            }

            // 随机选择一个服务提供者（软负载均衡）
            Service service = serviceList.get(random.nextInt(serviceList.size()));

            // 构造request对象
            ZRpcRequest request = new ZRpcRequest();
            request.setClassName(serviceName);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setVersion("version");
            request.setRequestId(UUID.randomUUID().toString());

            // 协议
            ZRpcResponse response = nettyClient.start(request,service);
            log.info("proxy success");
            if (response.getError() != null) {
                log.error("clientProxyFactory getError : " + response.getError());
            }
            return response.getResult();
        }
    }


    public ServiceDiscover getServiceDiscover() {
        return serviceDiscover;
    }

    public void setServiceDiscover(ServiceDiscover serviceDiscover) {
        this.serviceDiscover = serviceDiscover;
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }

    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    public Map<Class<?>, Object> getObjectCache() {
        return objectCache;
    }

    public void setObjectCache(Map<Class<?>, Object> objectCache) {
        this.objectCache = objectCache;
    }

}
