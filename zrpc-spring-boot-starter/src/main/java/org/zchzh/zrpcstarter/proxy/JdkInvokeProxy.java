package org.zchzh.zrpcstarter.proxy;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
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
