package org.zchzh.zrpcstarter.remote.client;

import io.netty.util.concurrent.Promise;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.remote.handler.ResponseHandler;

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

    /**
     * 发起请求
     * @param request 请求数据
     * @return 返回结果future
     */
    Promise<ZRpcResponse> invoke(ZRpcRequest request);


    ResponseHandler getHandler() throws InterruptedException, ExecutionException;

//    /**
//     * 发送请求
//     * @param request 请求信息
//     */
//    void send(ZRpcRequest request);
}
