package org.zchzh.zrpcstarter.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.zchzh.zrpcstarter.protocol.service.ServiceObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengchzh
 * @date 2021/4/16
 */

@Slf4j
public class NacosServiceDiscover implements ServiceDiscover{


    private NamingService namingService;

    public NacosServiceDiscover(String addr) {
        try {
            namingService = NamingFactory.createNamingService(addr);
        }catch (NacosException e) {
            log.error("nacos discover error - {}", e.getErrMsg());
        }

    }

    @Override
    public List<ServiceObject> getService(String name) {
        try {
            return convert(namingService.getAllInstances(name));
        }catch (NacosException e) {
            log.error("nacos register error - {}", e.getErrMsg());
            return new ArrayList<>();
        }
    }

    private List<ServiceObject> convert(List<Instance> list) {
        List<ServiceObject> services = new ArrayList<>();
        for (Instance instance: list) {
            ServiceObject serviceObject = new ServiceObject(instance.getServiceName());
            serviceObject.setAddress(instance.getIp() + ":" + instance.getPort());
            serviceObject.setProtocol(instance.getMetadata().get("protocol"));
            services.add(serviceObject);
        }
        return services;
    }
}
