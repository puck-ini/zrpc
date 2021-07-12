package org.zchzh.zrpcstarter.config;

import com.alibaba.nacos.api.exception.NacosException;
import org.zchzh.zrpcstarter.register.discovery.NacosServiceDiscover;
import org.zchzh.zrpcstarter.register.discovery.ServiceDiscover;
import org.zchzh.zrpcstarter.register.discovery.ZkServiceDiscover;
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

//@Configuration
public class AutoConfiguration {

    @Bean
    public DefaultRpcProcessorListener defaultRpcProcessor() {
        return new DefaultRpcProcessorListener();
    }

    @Bean
    public RpcProperties zRpcProperty() {
        return new RpcProperties();
    }
    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired RpcProperties rpcProperties) throws NacosException {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();

        // 设置服务发现者
        switch (rpcProperties.getRegisterProtocol()) {
            case "zookeeper": {
                clientProxyFactory.setServiceDiscover(new ZkServiceDiscover(rpcProperties.getRegisterAddress()));
                break;
            }
            case "nacos": {
                clientProxyFactory.setServiceDiscover(new NacosServiceDiscover(rpcProperties.getRegisterAddress()));
                break;
            }
            default: {
                throw new RuntimeException("注册中心启动失败");
            }
        }
        return clientProxyFactory;
    }

    @Bean
    public ServiceRegister serviceRegistry(@Autowired RpcProperties rpcProperties) throws NacosException {
        switch (rpcProperties.getRegisterProtocol()) {
            case "zookeeper": {
                return new ZookeeperServiceRegister(
                        rpcProperties.getRegisterAddress(),
                        rpcProperties.getServerPort(),
                        rpcProperties.getProtocol());
            }
            case "nacos": {
                return new NacosServiceRegister(
                        rpcProperties.getRegisterAddress(),
                        rpcProperties.getServerPort(),
                        rpcProperties.getProtocol());
            }
            default: {
                throw new RuntimeException("注册中心启动失败");
            }
        }
    }

    @Bean
    public ServiceDiscover serviceDiscover(@Autowired RpcProperties rpcProperties) {
        switch (rpcProperties.getRegisterProtocol()) {
            case "zookeeper": {
                return new ZkServiceDiscover(rpcProperties.getRegisterAddress());
            }
            case "nacos": {
                return new NacosServiceDiscover(rpcProperties.getRegisterAddress());
            }
            default: {
                throw new RuntimeException("服务发现启动失败");
            }
        }
    }

    @Bean
    public Server server(@Autowired RpcProperties rpcProperties){
        return new NettyServer(rpcProperties.getServerPort(), rpcProperties.getSerializer());
    }
}
