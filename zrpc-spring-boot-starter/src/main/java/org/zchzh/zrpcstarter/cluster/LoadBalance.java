package org.zchzh.zrpcstarter.cluster;

import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.List;

/**
 * @author zengchzh
 * @date 2021/5/23
 *
 * 负载均衡
 */
public interface LoadBalance {

    /**
     * 通过负载均衡获取服务
     * @param list 服务列表
     * @return 返回服务
     */
    ServiceObject get(List<ServiceObject> list);
}
