package org.zchzh.zrpcstarter.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/4/27
 */
@Slf4j
public class NettyClient implements Client {


    private String ip;

    private int port;

    private EventLoopGroup workGroup = new NioEventLoopGroup(1);

    private final Promise<NettyClientHandler> promiseHandler = ImmediateEventExecutor.INSTANCE.newPromise();

    private final Promise<Channel> promiseChannel = ImmediateEventExecutor.INSTANCE.newPromise();

    private Bootstrap bootstrap;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.start();
    }

    @Override
    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyClientInitializer());
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        if (channelActive()) {
            return;
        }

        ChannelFuture future = bootstrap.connect(ip,port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    NettyClientHandler handler = futureListener.channel().pipeline().get(NettyClientHandler.class);
                    promiseChannel.trySuccess(futureListener.channel());
                    promiseHandler.trySuccess(handler);
                    log.info("connect success");
                } else {
                    log.info("Failed to connect to server, try connect after 10s");

                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            connect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }


    @Override
    public NettyClientHandler getHandler() throws InterruptedException, ExecutionException {
//        promiseHandler.await();
//        if (promiseHandler.isSuccess()) {
//            return promiseHandler.getNow();
//        }
        return promiseHandler.get();
    }

    private boolean channelActive() {
        Channel channel = promiseChannel.getNow();
        return channel != null && channel.isActive();
    }


}
