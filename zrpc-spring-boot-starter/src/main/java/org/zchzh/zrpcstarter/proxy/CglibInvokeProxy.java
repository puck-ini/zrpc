package org.zchzh.zrpcstarter.proxy;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.register.Register;

import java.lang.reflect.Method;

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

    private class ClientInvocationHandler extends AbstractInvocationHandler implements MethodInterceptor {

        public ClientInvocationHandler(Class<?> clazz) {
            super(clazz);
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return handler(o, method, objects, register);
        }
    }
}
