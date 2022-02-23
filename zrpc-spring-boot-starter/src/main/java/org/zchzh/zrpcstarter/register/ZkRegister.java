package org.zchzh.zrpcstarter.register;

import com.google.auto.service.AutoService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.springframework.util.CollectionUtils;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zengchzh
 * @date 2021/7/19
 */

@AutoService(Register.class)
@JdkSPI(Constants.ZK)
public class ZkRegister implements Register {

    private ZkClient zkClient;

    private static final String ROOT_NODE = "/zrpc/service";

    private static final String NODE_SEPARATOR = "/";

    @Override
    public Register init(String address) {
        zkClient = new ZkClient(address);
        zkClient.setZkSerializer(new SerializableSerializer());
        if (!zkClient.exists(ROOT_NODE)) {
            zkClient.createPersistent(ROOT_NODE, true);
        }
        return this;
    }

    @Override
    public ServiceObject register(ServiceObject serviceObject) {
        String serviceRootPath = ROOT_NODE + NODE_SEPARATOR + serviceObject.getServiceName();
        if (!zkClient.exists(serviceRootPath)) {
            zkClient.createPersistent(serviceRootPath, true);
        }
        String childPath = serviceRootPath + NODE_SEPARATOR + serviceObject.getAddress();
        if (zkClient.exists(childPath)) {
            zkClient.delete(childPath);
        }
        serviceObject.setClazz(null);
        zkClient.createEphemeral(childPath, serviceObject);
        return serviceObject;
    }

    @Override
    public List<ServiceObject> getAll() {
        List<String> childPath = zkClient.getChildren(ROOT_NODE);
        List<ServiceObject> result = new ArrayList<>();
        for (String path : childPath) {
            result.addAll(getAll(NODE_SEPARATOR + path));
        }
        return result;
    }

    @Override
    public List<ServiceObject> getAll(String serviceName) {
        return getService(serviceName);
    }


    private List<ServiceObject> getService(String serviceName) {
        String serviceRootPath = ROOT_NODE + NODE_SEPARATOR + serviceName;
        List<String> childPath = zkClient.getChildren(serviceRootPath);
        List<ServiceObject> result = ServiceCache.getCache(serviceName);
        if (CollectionUtils.isEmpty(result)) {
            for (String path : childPath) {
                String nodePath = serviceRootPath + NODE_SEPARATOR + path;
                ServiceObject so = zkClient.readData(nodePath);
                result.add(so);
            }
            ServiceCache.putCache(serviceName, result);
            subService(serviceName);
        }
        return result;
    }

    private void subService(String serviceName) {
        String serviceRootPath = ROOT_NODE + NODE_SEPARATOR + serviceName;
        IZkChildListener childListener = (s, list) -> {
            List<ServiceObject> serviceObjectList = new CopyOnWriteArrayList<>();
            for (String path : list) {
                String nodePath = serviceRootPath + NODE_SEPARATOR + path;
                serviceObjectList.add(zkClient.readData(nodePath));
            }
            ServiceCache.putCache(serviceName, serviceObjectList);
        };
        zkClient.subscribeChildChanges(serviceRootPath, childListener);
    }
}
