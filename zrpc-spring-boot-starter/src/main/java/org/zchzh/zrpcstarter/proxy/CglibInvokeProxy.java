package org.zchzh.zrpcstarter.proxy;

import com.google.auto.service.AutoService;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.cluster.LoadBalanceFactory;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.client.ClientServiceCache;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zengchzh
 * @date 2021/7/22
 */

@Slf4j
@AutoService(InvokeProxy.class)
@JdkSPI(Constants.CGLIB)
public class CglibInvokeProxy implements InvokeProxy {

    private Register register;

    @Override
    public Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ClientInvocationHandler(clazz));
        return enhancer.create();
    }

    @Override
    public void setDiscovery(Register register) {
        this.register = register;
    }

    private class ClientInvocationHandler implements MethodInterceptor {
        private final Class<?> clazz;

        public ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }

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
                    .parameters(objects)
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
