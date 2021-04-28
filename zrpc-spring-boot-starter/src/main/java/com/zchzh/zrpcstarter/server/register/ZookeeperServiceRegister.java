package com.zchzh.zrpcstarter.server.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.zchzh.zrpcstarter.config.Constants;
import com.zchzh.zrpcstarter.protocol.service.Service;
import com.zchzh.zrpcstarter.protocol.service.ServiceObject;
import com.zchzh.zrpcstarter.serializer.zookeeper.ZookeeperSerializer;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
@Deprecated
public class ZookeeperServiceRegister extends DefaultServiceRegister implements ServiceRegister {

    private ZkClient zkClient;

    public ZookeeperServiceRegister(String zkAddress, Integer port, String protocol) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
        this.port = port;
        this.protocol = protocol;
    }
    /**
     * 服务注册
     *
     * @param so 服务持有者
     * @throws Exception 注册异常
     */
    @Override
    public void register(ServiceObject so) throws UnknownHostException, NacosException {
        super.register(so);
        Service service = new Service();

        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + port;
        service.setAddress(address);
        service.setName(so.getClassName());
        service.setProtocol(protocol);
        this.exportService(service);

    }

    /**
     * 服务暴露
     *
     * @param serviceResource 需要暴露的服务信息
     */
    private void exportService(Service serviceResource) {
        String serviceName = serviceResource.getName();
        String uri = JSON.toJSONString(serviceResource);
        try {
            uri = URLEncoder.encode(uri, Constants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = "/testrpc" + "/" + serviceName + "/service";
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
        }
        String uriPath = servicePath + "/" + uri;
        if (zkClient.exists(uriPath)) {
            zkClient.delete(uriPath);
        }
        zkClient.createEphemeral(uriPath);
    }

}
