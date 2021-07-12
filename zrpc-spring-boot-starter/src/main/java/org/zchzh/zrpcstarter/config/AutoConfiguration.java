package org.zchzh.zrpcstarter.config;

import com.alibaba.nacos.api.exception.NacosException;
import org.zchzh.zrpcstarter.register.discovery.NacosServiceDiscover;
import org.zchzh.zrpcstarter.register.discovery.ServiceDiscover;
import org.zchzh.zrpcstarter.register.discovery.ZkServiceDiscover;
import org.zchzh.zrpcstarter.properties.ZRpcProperty;
import org.zchzh.zrpcstarter.proxy.ClientProxyFactory;;
import org.zchzh.zrpcstarter.remote.server.NettyServer;
import org.zchzh.zrpcstarter.register.NacosServiceRegister;
import org.zchzh.zrpcstarter.register.ServiceRegister;
import org.zchzh.zrpcstarter.register.ZookeeperServiceRegister;
import org.zchzh.zrpcstarter.remote.server.Server;
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
    public ServiceDiscover serviceDiscover(@Autowired ZRpcProperty zRpcProperty) {
        switch (zRpcProperty.getRegisterProtocol()) {
            case "zookeeper": {
                return new ZkServiceDiscover(zRpcProperty.getRegisterAddress());
            }
            case "nacos": {
                return new NacosServiceDiscover(zRpcProperty.getRegisterAddress());
            }
            default: {
                throw new RuntimeException("服务发现启动失败");
            }
        }
    }

    @Bean
    public Server server(@Autowired ZRpcProperty zRpcProperty){
        return new NettyServer(zRpcProperty.getServerPort(), zRpcProperty.getSerializer());
    }
}
