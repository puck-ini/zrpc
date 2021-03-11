package com.zchzh.zrpc.config;

import com.zchzh.zrpc.client.NettyClient;
import com.zchzh.zrpc.client.discovery.ZkServiceDiscover;
import com.zchzh.zrpc.listener.DefaultRpcProcessor;
import com.zchzh.zrpc.properties.ZRpcProperty;
import com.zchzh.zrpc.proxy.ClientProxyFactory;;
import com.zchzh.zrpc.server.NettyServer;
import com.zchzh.zrpc.server.Server;
import com.zchzh.zrpc.server.register.ServiceRegister;
import com.zchzh.zrpc.server.register.ZookeeperServiceRegister;
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
        clientProxyFactory.setNettyClient(new NettyClient());
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
    public Server server(@Autowired ZRpcProperty zRpcProperty){
        return new NettyServer(zRpcProperty.getServerPort());
    }
}
