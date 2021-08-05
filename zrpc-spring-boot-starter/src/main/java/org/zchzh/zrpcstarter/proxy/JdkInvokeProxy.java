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

    private Register register;

    @Override
    public Object getProxy(Class<?> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new ClientInvocationHandler(clazz));
    }

    @Override
    public void setDiscovery(Register register) {
        this.register = register;
    }

    private class ClientInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {

        public ClientInvocationHandler(Class<?> clazz) {
            super(clazz);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return handler(proxy, method, args, register);
        }
    }
}
