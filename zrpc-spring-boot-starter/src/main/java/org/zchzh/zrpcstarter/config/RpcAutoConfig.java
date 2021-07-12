package org.zchzh.zrpcstarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zchzh.zrpcstarter.proxy.JdkProxyFactory;
import org.zchzh.zrpcstarter.proxy.ProxyFactory;
import org.zchzh.zrpcstarter.register.NacosRegister;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.server.NettyServer;
import org.zchzh.zrpcstarter.remote.server.Server;

/**
 * @author zengchzh
 * @date 2021/7/4
 */
@Configuration
public class RpcAutoConfig {

    @Bean
    public RpcProperties rpcProperties() {
        return new RpcProperties();
    }

    @Bean
    public Server server(@Autowired RpcProperties rpcProperties) {
        return new NettyServer(rpcProperties.getServerPort());
    }

    @Bean
    public RpcServerStarter rpcServerStarter() {
        return new RpcServerStarter();
    }

    @Bean
    public RpcClientStarter rpcClientStarter() {
        return new RpcClientStarter();
    }

    @Bean
    public Register register(@Autowired RpcProperties prop) {
        return new NacosRegister(prop.getRegisterAddress());
    }

    @Bean
    public ProxyFactory proxyFactory() {
        return new JdkProxyFactory();
    }
}
