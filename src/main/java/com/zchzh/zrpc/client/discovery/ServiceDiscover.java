package com.zchzh.zrpc.client.discovery;

import com.zchzh.zrpc.model.service.Service;

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
    List<Service> getService(String name);
}
