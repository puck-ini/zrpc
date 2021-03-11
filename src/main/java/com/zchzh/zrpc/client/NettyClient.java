package com.zchzh.zrpc.client;

import com.zchzh.zrpc.model.request.ZRpcRequest;
import com.zchzh.zrpc.model.respones.ZRpcResponse;
import com.zchzh.zrpc.server.NettyServerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public class NettyClient extends Client{

    private Channel channel;

    private NettyClientHandler nettyClientHandler;

    private ZRpcResponse zRpcResponse;

    @Override
    public void start(ZRpcRequest request) {
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

    }

    @Override
    public void stop() {
        this.channel.close();
    }
}
