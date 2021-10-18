package org.zchzh.zrpcstarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.RpcProp;
import org.zchzh.zrpcstarter.proxy.InvokeProxy;
import org.zchzh.zrpcstarter.register.Register;

/**
 * @author zengchzh
 * @date 2021/7/19
 */
@Configuration
public class RpcClientAutoConfig {

    @Bean
    public RpcClientProperties rpcClientProperties() {
        RpcClientProperties properties = new RpcClientProperties();
        RpcProp.INSTANCE.put(properties);
        return properties;
    }

    @Bean
    public InjectServiceProcessor rpcClientStarter() {
        return new InjectServiceProcessor();
    }

    @Bean(name = "discovery")
    public Register register(@Autowired RpcClientProperties rpcClientProperties) {
        Register register = (Register) FactoryProducer.INSTANCE.getInstance(Constants.REGISTER)
                .getInstance(rpcClientProperties.getRegisterProtocol());
        return register.init(rpcClientProperties.getRegisterAddress());
    }


    @Bean
    public InvokeProxy invokeProxy(@Autowired @Qualifier("discovery") Register discovery,
                                   @Autowired RpcClientProperties rpcClientProperties) {
        InvokeProxy invokeProxy = (InvokeProxy) FactoryProducer.INSTANCE.getInstance(Constants.PROXY)
                .getInstance(rpcClientProperties.getProxy());
        invokeProxy.setDiscovery(discovery);
        return invokeProxy;
    }
}
