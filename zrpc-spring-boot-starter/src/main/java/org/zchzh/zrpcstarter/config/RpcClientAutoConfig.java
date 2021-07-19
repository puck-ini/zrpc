package org.zchzh.zrpcstarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zchzh.zrpcstarter.proxy.JdkProxyFactory;
import org.zchzh.zrpcstarter.proxy.ProxyFactory;
import org.zchzh.zrpcstarter.register.NacosRegister;
import org.zchzh.zrpcstarter.register.Register;

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
        return new NacosRegister(rpcClientProperties.getRegisterAddress());
    }


    @Bean
    public ProxyFactory proxyFactory() {
        return new JdkProxyFactory();
    }
}
