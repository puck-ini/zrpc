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
     * 获取监听的端口
     * @return 监听端口
     */
    int getPort();

}
