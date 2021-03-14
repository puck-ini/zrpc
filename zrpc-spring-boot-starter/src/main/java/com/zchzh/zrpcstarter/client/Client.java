package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import com.zchzh.zrpcstarter.protocol.service.Service;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public abstract class Client {

    /**
     * 服务ip
     */
    protected String ip;

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
     * @param request 请求数据
     */
    public abstract ZRpcResponse start(ZRpcRequest request, Service service);

    /**
     * 停止
     */
    public abstract void stop();
}
