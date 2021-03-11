package com.zchzh.zrpc.client.discovery;

import com.alibaba.fastjson.JSON;
import com.zchzh.zrpc.model.service.Service;
import com.zchzh.zrpc.serializer.zookeeper.ZookeeperSerializer;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public class ZkServiceDiscover implements ServiceDiscover{

    private ZkClient zkClient;

    public ZkServiceDiscover(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
    }
    @Override
    public List<Service> getService(String name) {
        String servicePath = "/testrpc" + "/"+ name + "/service";
        List<String> children = zkClient.getChildren(servicePath);
        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str -> {
            String deCh = null;
            try {
                deCh = URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return JSON.parseObject(deCh, Service.class);
        }).collect(Collectors.toList());
    }
}
