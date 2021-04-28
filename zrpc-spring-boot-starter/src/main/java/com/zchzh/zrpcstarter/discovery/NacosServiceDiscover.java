package com.zchzh.zrpcstarter.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zchzh.zrpcstarter.protocol.service.ServiceObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengchzh
 * @date 2021/4/16
 */
public class NacosServiceDiscover implements ServiceDiscover{


    private NamingService namingService;

    public NacosServiceDiscover(String addr) throws NacosException {
        namingService = NamingFactory.createNamingService(addr);
    }

    @Override
    public List<ServiceObject> getService(String name) throws NacosException {
        return convert(namingService.getAllInstances(name));
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
