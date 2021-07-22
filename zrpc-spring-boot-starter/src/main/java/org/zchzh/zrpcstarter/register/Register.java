package org.zchzh.zrpcstarter.register;


import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.List;

/**
 * @author zengchzh
 * @date 2021/7/4
 */
public interface Register {

    /**
     * 初始化配置
     * @param address 注册中心地址
     * @return 返回 Register 对象
     */
    Register init(String address);
    /**
     * 注册服务
     * @param serviceObject 服务数据
     * @return 返回注册的服务
     */
    ServiceObject register(ServiceObject serviceObject);

    /**
     * 返回所有注册的服务
     * @return 服务列表
     */
    List<ServiceObject> getAll();

    /**
     * 通过服务名获取服务列表
     * @param serviceName 服务名
     * @return 服务列表
     */
    List<ServiceObject> getAll(String serviceName);


}
