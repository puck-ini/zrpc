package org.zchzh.zrpcstarter.register.discovery;

import org.zchzh.zrpcstarter.model.ServiceObject;
import org.zchzh.zrpcstarter.serializer.zookeeper.ZookeeperSerializer;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
@Deprecated
public class ZkServiceDiscover implements ServiceDiscover{

    private ZkClient zkClient;

    public ZkServiceDiscover(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
    }
    @Override
    public List<ServiceObject> getService(String name) {
//        String servicePath = "/testrpc" + "/"+ name + "/service";
//        List<String> children = zkClient.getChildren(servicePath);
//        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str -> {
//            String deCh = null;
//            try {
//                deCh = URLDecoder.decode(str, Constants.UTF_8);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            return JSON.parseObject(deCh, Service.class);
//        }).collect(Collectors.toList());
        return null;
    }
}
