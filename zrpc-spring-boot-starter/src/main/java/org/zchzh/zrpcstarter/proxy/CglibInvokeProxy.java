package org.zchzh.zrpcstarter.proxy;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.RpcProp;
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

    @Override
    public Object getProxy(Class<?> clazz, String loadBalance) {
        Register discovery = (Register) FactoryProducer.INSTANCE
                .getInstance(Constants.REGISTER)
                .getInstance(RpcProp.INSTANCE.getClient().getRegisterProtocol());
        LoadBalance loadBalance1 = (LoadBalance) FactoryProducer.INSTANCE
                .getInstance(Constants.LOAD_BALANCE)
                .getInstance(loadBalance);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ClientInvocationHandler(clazz, discovery, loadBalance1));
        return enhancer.create();
    }

    private static class ClientInvocationHandler extends AbstractInvocationHandler implements MethodInterceptor {

        public ClientInvocationHandler(Class<?> clazz, Register discovery, LoadBalance loadBalance) {
            super(clazz, discovery, loadBalance);
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return handler(o, method, objects);
        }
    }
}
