package org.zchzh.zrpcstarter.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengchzh
 * @date 2021/7/4
 */
@Slf4j
@AutoService(Register.class)
@JdkSPI(Constants.NACOS)
public class NacosRegister extends AbstractRegister implements Register {

    private NamingService namingService;

//    public NacosRegister(String address) {
//        try {
//            namingService = NamingFactory.createNamingService(address);
//        } catch (NacosException e) {
//            log.error("nacos error", e);
//        }
//    }

    @Override
    public Register init(String address) {
        try {
            namingService = NamingFactory.createNamingService(address);
        } catch (NacosException e) {
            log.error("nacos error", e);
        }
        return this;
    }

    @Override
    public ServiceObject register(ServiceObject serviceObject) {
        Instance instance =toInstance(serviceObject);
        try {
            namingService.registerInstance(serviceObject.getServiceName(), instance);
        } catch (NacosException e) {
            log.error("nacos register fail - ", e);
            return null;
        }
        return serviceObject;
    }

    @Override
    public List<ServiceObject> getAll() {
        List<ServiceObject> serviceObjectList = new ArrayList<>();
        try {
            ListView<String> services = namingService.getServicesOfServer(1, Integer.MAX_VALUE);
            for (String serviceName : services.getData()) {
                serviceObjectList.addAll(getAll(serviceName));
            }
        } catch (NacosException e) {
            log.error(e.getErrMsg(), e);
        }
        return serviceObjectList;
    }

    @Override
    public List<ServiceObject> getAll(String serviceName) {
        List<ServiceObject> serviceObjectList = new ArrayList<>();
        try {
            serviceObjectList = getService(serviceName);
        } catch (NacosException e) {
            log.error(e.getErrMsg(), e);
        }
        return serviceObjectList;
    }

    private List<ServiceObject> getService(String serviceName) throws NacosException {
        List<ServiceObject> serviceObjectList = get(serviceName);
        if (CollectionUtils.isEmpty(serviceObjectList)) {
            List<Instance> instanceList = namingService.getAllInstances(serviceName);
            for (Instance instance : instanceList) {
                serviceObjectList.add(toService(instance));
            }
            subService(serviceName);
        }
        return serviceObjectList;
    }

    private void subService(String serviceName) throws NacosException {
        namingService.subscribe(serviceName, new EventListener() {
            @Override
            public void onEvent(Event event) {
                if (event instanceof NamingEvent) {
                    List<ServiceObject> serviceObjectList = new ArrayList<>();
                    for (Instance instance : ((NamingEvent) event).getInstances()) {
                        log.info(toService(instance).toString());
                        serviceObjectList.add(toService(instance));
                    }
                    put(serviceName, serviceObjectList);
                }
            }
        });
    }

    private ServiceObject toService(Instance instance) {
        return ServiceObject.builder()
                .serviceName(getServerName(instance.getServiceName()))
                .ip(instance.getIp())
                .port(instance.getPort())
                .weight(instance.getWeight())
                .meta(instance.getMetadata())
                .build();
    }

    private static final String CUT = "@@";

    private String getServerName(String name) {
        if (name == null) {
            return null;
        }
        return name.split(CUT)[1];
    }

    private Instance toInstance(ServiceObject serviceObject) {
        Instance instance = new Instance();
        instance.setServiceName(serviceObject.getServiceName());
        instance.setIp(serviceObject.getIp());
        instance.setPort(serviceObject.getPort());
        instance.setWeight(serviceObject.getWeight());
        instance.setMetadata(serviceObject.getMeta());
        return instance;
    }
}
