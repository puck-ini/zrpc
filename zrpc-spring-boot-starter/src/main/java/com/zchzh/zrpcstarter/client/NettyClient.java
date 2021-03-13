package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.model.request.ZRpcRequest;
import com.zchzh.zrpcstarter.model.respones.ZRpcResponse;
import com.zchzh.zrpcstarter.model.service.Service;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public class NettyClient extends Client{

    private Channel channel;

    private NettyClientHandler nettyClientHandler;

    private ZRpcResponse zRpcResponse;

    /**
     *
     * @param request 请求数据
     * @param service 注册中心中的服务信息
     * @return
     */
    @Override
    public ZRpcResponse start(ZRpcRequest request, Service service) {
        String[] addInfoArray = service.getAddress().split(":");
        ip= addInfoArray[0];
//        ip = "127.0.0.1";
        port = Integer.parseInt(addInfoArray[1]);

        nettyClientHandler = new NettyClientHandler(request);
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyClientInitializer(nettyClientHandler));

            // 连接服务器
            ChannelFuture future = bootstrap.connect(ip, port).sync();
            // 写入请求数据
            this.channel = future.channel();
            this.zRpcResponse = nettyClientHandler.getResponse();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            // 释放资源
            eventLoopGroup.shutdownGracefully();
        }
        return this.zRpcResponse;
    }

    @Override
    public void stop() {
        this.channel.close();
    }
}
