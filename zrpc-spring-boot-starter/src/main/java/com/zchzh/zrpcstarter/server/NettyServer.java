package com.zchzh.zrpcstarter.server;

import com.zchzh.zrpcstarter.server.NettyServerInitializer;
import com.zchzh.zrpcstarter.util.ServiceUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Slf4j
public class NettyServer extends Server{

    private Channel channel;

    private Map<String, Object> serviceMap = new HashMap<>();

    private final String serializerName;

    public NettyServer(int port, String serializerName){
        super(port);
        this.serializerName = serializerName;
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    ServerBootstrap serverBootstrap = new ServerBootstrap();;
                    serverBootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 100)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .childHandler(new NettyServerInitializer(serviceMap, serializerName));

                    // 启动服务
                    ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
                    channel = channelFuture.channel();
                    // 等待服务通道关闭
                    channelFuture.channel().closeFuture().sync();

                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        this.channel.close();
    }

    @Override
    public void addService(String name, Object object) {
        String serviceKey = ServiceUtil.makeServiceKey(name, "version");
        serviceMap.put(serviceKey, object);
    }
}
