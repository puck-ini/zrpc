package org.zchzh.zrpcstarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zchzh.zrpcstarter.proxy.InvokeProxyFactory;
import org.zchzh.zrpcstarter.proxy.JdkInvokeProxy;
import org.zchzh.zrpcstarter.proxy.InvokeProxy;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.register.RegisterFactory;

/**
 * @author zengchzh
 * @date 2021/7/19
 */
@Configuration
public class RpcClientAutoConfig {

    @Bean
    public RpcClientProperties rpcClientProperties() {
        return new RpcClientProperties();
    }

    @Bean
    public RpcClientStarter rpcClientStarter() {
        return new RpcClientStarter();
    }

    @Bean(name = "discovery")
    public Register register(@Autowired RpcClientProperties rpcClientProperties) {
        String name = rpcClientProperties.getRegisterProtocol() + ":" + rpcClientProperties.getRegisterAddress();
        return RegisterFactory.getInstance(name);
    }


    @Bean
    public InvokeProxy invokeProxy(@Autowired @Qualifier("discovery") Register discovery,
                                   @Autowired RpcClientProperties rpcClientProperties) {
        InvokeProxy invokeProxy = InvokeProxyFactory.getInstance(rpcClientProperties.getProxy());
        invokeProxy.setDiscovery(discovery);
        return invokeProxy;
    }
}
