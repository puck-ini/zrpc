package org.zchzh.zrpcstarter.proxy;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.RpcProp;
import org.zchzh.zrpcstarter.register.Register;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zengchzh
 * @date 2021/7/10
 */

@Slf4j
@AutoService(InvokeProxy.class)
@JdkSPI(Constants.JDK)
public class JdkInvokeProxy implements InvokeProxy {


    @Override
    public Object getProxy(Class<?> clazz, String loadBalance) {
        Register discovery = (Register) FactoryProducer.INSTANCE
                .getInstance(Constants.REGISTER)
                .getInstance(RpcProp.INSTANCE.getClient().getRegisterProtocol());
        LoadBalance loadBalance1 = (LoadBalance) FactoryProducer.INSTANCE
                .getInstance(Constants.LOAD_BALANCE)
                .getInstance(loadBalance);
        return Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[] {clazz},
                new ClientInvocationHandler(clazz, discovery, loadBalance1));
    }

    private static class ClientInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {

        public ClientInvocationHandler(Class<?> clazz, Register discovery, LoadBalance loadBalance) {
            super(clazz, discovery, loadBalance);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return handler(proxy, method, args);
        }
    }
}
