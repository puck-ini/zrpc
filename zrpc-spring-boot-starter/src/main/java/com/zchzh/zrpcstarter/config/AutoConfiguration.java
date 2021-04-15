package com.zchzh.zrpcstarter.config;

import com.zchzh.zrpcstarter.client.NettyClient;
import com.zchzh.zrpcstarter.client.discovery.ZkServiceDiscover;
import com.zchzh.zrpcstarter.listener.DefaultRpcProcessor;
import com.zchzh.zrpcstarter.properties.ZRpcProperty;
import com.zchzh.zrpcstarter.proxy.ClientProxyFactory;;
import com.zchzh.zrpcstarter.server.NettyServer;
import com.zchzh.zrpcstarter.server.AbstractServer;
import com.zchzh.zrpcstarter.server.register.ServiceRegister;
import com.zchzh.zrpcstarter.server.register.ZookeeperServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Configuration
public class AutoConfiguration {

    @Bean
    public DefaultRpcProcessor defaultRpcProcessor() {
        return new DefaultRpcProcessor();
    }

    @Bean
    public ZRpcProperty zRpcProperty() {
        return new ZRpcProperty();
    }
    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired ZRpcProperty zRpcProperty) {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();

        // 设置服务发现者
        clientProxyFactory.setServiceDiscover(new ZkServiceDiscover(zRpcProperty.getRegisterAddress()));

        // 设置支持的协议

        // 设置网络层
        clientProxyFactory.setNettyClient(new NettyClient(zRpcProperty.getSerializer()));
        return clientProxyFactory;
    }

    @Bean
    public ServiceRegister serviceRegistry(@Autowired ZRpcProperty zRpcProperty) {
        return new ZookeeperServiceRegister(
                zRpcProperty.getRegisterAddress(),
                zRpcProperty.getServerPort(),
                zRpcProperty.getProtocol());
    }

    @Bean
    public AbstractServer server(@Autowired ZRpcProperty zRpcProperty){
        return new NettyServer(zRpcProperty.getServerPort(), zRpcProperty.getSerializer());
    }
}
