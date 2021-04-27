package com.zchzh.zrpcstarter.server.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zchzh.zrpcstarter.protocol.service.ServiceObject;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengchzh
 * @date 2021/4/16
 */
public class NacosServiceRegister extends DefaultServiceRegister implements ServiceRegister, EnvironmentAware {



    private Environment environment;

    private NamingService namingService;

    public NacosServiceRegister(String addr, int port, String protocol) throws NacosException {
        namingService = NamingFactory.createNamingService(addr);
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public void register(ServiceObject so) throws UnknownHostException, NacosException {
        super.register(so);
        String host = InetAddress.getLocalHost().getHostAddress();
        Instance instance = new Instance();
        instance.setIp(host);
        instance.setPort(port);
        instance.setHealthy(false);
        instance.setWeight(1.0);
        Map<String, String> instanceMeta = new HashMap<>();
        instanceMeta.put("protocol", protocol);
        instance.setMetadata(instanceMeta);
        namingService.registerInstance(so.getName(), instance);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private String getHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    private void registerInstance() throws NacosException {
        NamingService naming = NamingFactory.createNamingService("127.0.0.1");
        naming.registerInstance("test","127.0.0.1", 8888);
    }

    private String getConfig(String prop){
        return environment.getProperty(prop);
    }

//
//    public static void main(String[] args) throws NacosException, InterruptedException {
//        NamingService naming = NamingFactory.createNamingService("127.0.0.1:8848");
////        naming.registerInstance("test","127.0.0.1", 8888);
//
//        Instance instance = new Instance();
//
//        instance.setIp("127.0.0.1");
//        instance.setPort(8888);
//        instance.setHealthy(false);
//        instance.setWeight(2.0);
//        Map<String, String> instanceMeta = new HashMap<>();
//        instanceMeta.put("addr", "123456");
//        instance.setMetadata(instanceMeta);
//        naming.registerInstance("test1", instance);
////        TimeUnit.SECONDS.sleep(60);
////        naming.deregisterInstance("test", "127.0.0.1", 8888);
//    }

}
