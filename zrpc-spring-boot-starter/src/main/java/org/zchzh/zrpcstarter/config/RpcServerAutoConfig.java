package org.zchzh.zrpcstarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.register.Register;
import org.zchzh.zrpcstarter.remote.server.NettyServer;
import org.zchzh.zrpcstarter.remote.server.Server;

/**
 * @author zengchzh
 * @date 2021/7/19
 */
@Configuration
public class RpcServerAutoConfig {

    @Bean
    public RpcServerProperties rpcServerProperties() {
        return new RpcServerProperties();
    }

    @Bean
    public Server server(@Autowired RpcServerProperties rpcServerProperties) {
        return new NettyServer(rpcServerProperties.getServerPort());
    }

    @Bean
    public RpcServerStarter rpcServerStarter() {
        return new RpcServerStarter();
    }

    @Bean
    public Register register(@Autowired RpcServerProperties rpcServerProperties) {
        Register register = (Register) FactoryProducer.INSTANCE.getInstance(Constants.REGISTER)
                .getInstance(rpcServerProperties.getRegisterProtocol());
        return register.init(rpcServerProperties.getRegisterAddress());
    }
}
