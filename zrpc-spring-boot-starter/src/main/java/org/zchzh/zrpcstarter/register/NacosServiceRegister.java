package org.zchzh.zrpcstarter.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.zchzh.zrpcstarter.protocol.service.ServiceObject;
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
public class NacosServiceRegister implements ServiceRegister, EnvironmentAware {

    protected String protocol;

    protected Integer port;

    private Environment environment;

    private final NamingService namingService;

    public NacosServiceRegister(String addr, int port, String protocol) throws NacosException {
        namingService = NamingFactory.createNamingService(addr);
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public void register(ServiceObject so) throws NacosException {
        Instance instance = newNacosInstance();
        instance.setMetadata(newNacosInstanceMeta());
        namingService.registerInstance(so.getName(), instance);
    }

    private Instance newNacosInstance() {
        Instance instance = new Instance();
        instance.setIp(getHost());
        instance.setPort(port);
        instance.setHealthy(false);
        instance.setWeight(1.0);
        return instance;
    }

    private Map<String, String> newNacosInstanceMeta() {
        Map<String, String> instanceMeta = new HashMap<>(16);
        instanceMeta.put("protocol", protocol);
        instanceMeta.put("address", getHost() + ":" + port);
        return instanceMeta;

    }
    private String getHost() {
        String host = "";
        try {
             host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private String getConfig(String prop){
        return environment.getProperty(prop);
    }

}
