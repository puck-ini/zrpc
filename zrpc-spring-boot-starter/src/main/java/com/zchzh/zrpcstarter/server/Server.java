package com.zchzh.zrpcstarter.server;

import lombok.Data;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Data
public abstract class Server {

    /**
     * 服务端口
     */
    protected int port;

    /**
     * 服务协议
     */
    protected String protocol;

    public Server(int port) {
        this.port = port;
    }


    /**
     * 启动
     */
    public abstract void start();

    /**
     * 停止
     */
    public abstract void stop();


    public abstract void addService(String name, Object object);
}
