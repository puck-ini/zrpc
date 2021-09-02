package org.zchzh.zrpcstarter.remote.client;

import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;

import java.util.concurrent.CompletableFuture;

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
     * 关闭client
     */
    void stop();

    /**
     * 发起请求
     * @param request 请求数据
     * @return 返回结果future
     */
    CompletableFuture<ZRpcResponse> invoke(ZRpcRequest request);

}
