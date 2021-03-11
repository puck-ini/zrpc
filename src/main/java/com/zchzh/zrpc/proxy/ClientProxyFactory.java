package com.zchzh.zrpc.proxy;

import com.zchzh.zrpc.client.NettyClient;
import com.zchzh.zrpc.client.discovery.ServiceDiscover;
import com.zchzh.zrpc.model.request.ZRpcRequest;
import com.zchzh.zrpc.model.respones.ZRpcResponse;
import com.zchzh.zrpc.model.service.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Data
public class ClientProxyFactory {

    private ServiceDiscover serviceDiscover;

    private NettyClient nettyClient;

    private Map<Class<?>, Object> objectCache = new HashMap<>();

    public <T> T getProxy(Class<T> clazz) {
        return (T) this.objectCache.computeIfAbsent(clazz,
                cls -> newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, new ClientInvocationHandler(cls)));
    }

    @Slf4j
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

            // 协议
            nettyClient.start(request);
            log.info("proxy success");
            return null;
        }
    }

}
