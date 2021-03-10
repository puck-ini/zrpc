package com.zchzh.zrpc.server;

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


    /**
     * 启动
     */
    public abstract void start();

    /**
     * 停止
     */
    public abstract void stop();
}
