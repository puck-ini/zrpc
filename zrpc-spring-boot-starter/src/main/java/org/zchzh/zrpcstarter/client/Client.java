package org.zchzh.zrpcstarter.client;

import java.util.concurrent.ExecutionException;

/**
 * @author zengchzh
 * @date 2021/4/27
 */
public interface Client {

    /**
     * 启动client
     */
    void start();

    /**
     * 连接server
     */
    void connect();


    NettyClientHandler getHandler() throws InterruptedException, ExecutionException;

//    /**
//     * 发送请求
//     * @param request 请求信息
//     */
//    void send(ZRpcRequest request);
}
