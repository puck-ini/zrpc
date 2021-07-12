package org.zchzh.zrpcstarter.remote.server;

/**
 * @author zengchzh
 * @date 2021/4/28
 */
public interface Server {

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();


    /**
     * 添加服务
     * @param name 服务名
     * @param object 服务实例
     */
    void addService(String name, Object object);
}
