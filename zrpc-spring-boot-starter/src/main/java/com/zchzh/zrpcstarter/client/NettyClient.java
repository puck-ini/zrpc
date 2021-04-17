package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.cache.GlobalCache;
import com.zchzh.zrpcstarter.config.Constants;
import com.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import com.zchzh.zrpcstarter.protocol.service.Service;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Slf4j
public class NettyClient extends AbstractClient {

    private NettyClientHandler nettyClientHandler;

    private ZRpcResponse zRpcResponse;

    private String serializerName;

    public NettyClient(){}

//    public NettyClient(String serializerName) {
//        this.serializerName = serializerName;
//    }

    /**
     *
     * @param request 请求数据
     * @param service 注册中心中的服务信息
     * @return
     */
    @Override
    public ZRpcResponse sendRequest(ZRpcRequest request, Service service) {
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
                    .handler(new NettyClientInitializer(nettyClientHandler, serializerName));

            // 连接服务器
            ChannelFuture future = bootstrap.connect(ip, port).sync();
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    NettyClientHandler handler = channelFuture.channel().pipeline().get(NettyClientHandler.class);
                    GlobalCache.INSTANCE.put(Constants.DEFAULT_HANDLE, handler);
                    log.info("NettyClient add handler");
                } else {
                    log.error("NettyClient add handler error");
                }
            });
            // 写入请求数据
//            this.zRpcResponse = nettyClientHandler.getResponse();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            // 释放资源
            eventLoopGroup.shutdownGracefully();
        }
        return this.zRpcResponse;
    }


    private String addr;

    public NettyClient(String addr) {
        this.addr = addr;
    }

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6,
            12,
            300,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000),
            r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("netty-client-" + r.hashCode());
                return thread;
            });


    @Override
    public void start() {
        threadPoolExecutor.execute(() -> {

            String[] addInfoArray = addr.split(":");
            String ip= addInfoArray[0];
            int port = Integer.parseInt(addInfoArray[1]);

            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new NettyClientInitializer());

                // 连接服务器
                ChannelFuture future = bootstrap.connect(ip, port).sync();
                future.addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        NettyClientHandler handler = channelFuture.channel().pipeline().get(NettyClientHandler.class);
                        GlobalCache.INSTANCE.put(Constants.DEFAULT_HANDLE, handler);
                        log.info("NettyClient add handler" + Thread.currentThread().getName());
                    } else {
                        log.error("NettyClient add handler error");
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                // 释放资源
//                eventLoopGroup.shutdownGracefully();
            }
        });

    }
}
