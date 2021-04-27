package com.zchzh.zrpcstarter.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.zchzh.zrpcstarter.client.discovery.NacosServiceDiscover;
import com.zchzh.zrpcstarter.client.discovery.ZkServiceDiscover;
import com.zchzh.zrpcstarter.listener.DefaultRpcProcessorListener;
import com.zchzh.zrpcstarter.properties.ZRpcProperty;
import com.zchzh.zrpcstarter.proxy.ClientProxyFactory;;
import com.zchzh.zrpcstarter.server.NettyServer;
import com.zchzh.zrpcstarter.server.AbstractServer;
import com.zchzh.zrpcstarter.server.register.NacosServiceRegister;
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
    public DefaultRpcProcessorListener defaultRpcProcessor() {
        return new DefaultRpcProcessorListener();
    }

    @Bean
    public ZRpcProperty zRpcProperty() {
        return new ZRpcProperty();
    }
    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired ZRpcProperty zRpcProperty) throws NacosException {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();

        // 设置服务发现者
        switch (zRpcProperty.getRegisterProtocol()) {
            case "zookeeper": {
                clientProxyFactory.setServiceDiscover(new ZkServiceDiscover(zRpcProperty.getRegisterAddress()));
                break;
            }
            case "nacos": {
                clientProxyFactory.setServiceDiscover(new NacosServiceDiscover(zRpcProperty.getRegisterAddress()));
                break;
            }
            default: {
                throw new RuntimeException("注册中心启动失败");
            }
        }


        // 设置支持的协议

        // 设置网络层
//        clientProxyFactory.setNettyClient(new NettyClient(zRpcProperty.getSerializer()));
        return clientProxyFactory;
    }

    @Bean
    public ServiceRegister serviceRegistry(@Autowired ZRpcProperty zRpcProperty) throws NacosException {
        switch (zRpcProperty.getRegisterProtocol()) {
            case "zookeeper": {
                return new ZookeeperServiceRegister(
                        zRpcProperty.getRegisterAddress(),
                        zRpcProperty.getServerPort(),
                        zRpcProperty.getProtocol());
            }
            case "nacos": {
                return new NacosServiceRegister(
                        zRpcProperty.getRegisterAddress(),
                        zRpcProperty.getServerPort(),
                        zRpcProperty.getProtocol());
            }
            default: {
                throw new RuntimeException("注册中心启动失败");
            }
        }

    }

    @Bean
    public AbstractServer server(@Autowired ZRpcProperty zRpcProperty){
        return new NettyServer(zRpcProperty.getServerPort(), zRpcProperty.getSerializer());
    }
}
