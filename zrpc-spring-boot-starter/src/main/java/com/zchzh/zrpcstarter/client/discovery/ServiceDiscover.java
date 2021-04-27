package com.zchzh.zrpcstarter.client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.zchzh.zrpcstarter.protocol.service.ServiceObject;

import java.util.List;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public interface ServiceDiscover {

    /**
     * 通过名字获取服务信息
     * @param name
     * @return
     */
    List<ServiceObject> getService(String name) throws NacosException;
}
