package org.zchzh.zrpcstarter.remote.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Slf4j
public class NettyServer implements Server {

    /**
     * 服务端口
     */
    private int port;

    private Channel channel;


    public NettyServer(int port){
        this.port = port;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();;
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
//                            .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyServerInitializer());

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

    @Override
    public void stop() {
        this.channel.close();
    }

    @Override
    public int getPort() {
        return this.port;
    }

}
