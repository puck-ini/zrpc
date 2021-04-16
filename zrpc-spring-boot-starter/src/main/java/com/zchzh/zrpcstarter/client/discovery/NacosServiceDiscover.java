package com.zchzh.zrpcstarter.client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zchzh.zrpcstarter.protocol.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    public List<Service> getService(String name) throws NacosException {
        return convert(namingService.getAllInstances(name));
    }

    private List<Service> convert(List<Instance> list) {
        List<Service> services = new ArrayList<>();
        for (Instance instance: list) {
            Service service = new Service();
            service.setAddress(instance.getIp() + ":" + instance.getPort());
            service.setName(instance.getServiceName());
            service.setProtocol(instance.getMetadata().get("protocol"));
            services.add(service);
        }
        return services;
    }
}
