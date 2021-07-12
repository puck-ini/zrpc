package org.zchzh.zrpcstarter.remote.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.model.ResponseMap;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.remote.handler.ResponseHandler;

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

    private final Promise<Channel> channelPromise = ImmediateEventExecutor.INSTANCE.newPromise();

    private Bootstrap bootstrap;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
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
            public void operationComplete(ChannelFuture future1) throws Exception {
                if (future1.isSuccess()) {
                    channelPromise.trySuccess(future1.channel());
                } else {
                    log.error("Failed to connect to server, try connect after 10s", future1.cause());
                    future1.channel().eventLoop().schedule(new Runnable() {
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
    public Promise<ZRpcResponse> invoke(ZRpcRequest request) {
        Promise<ZRpcResponse> promise = ImmediateEventExecutor.INSTANCE.newPromise();
        ResponseMap.put(request.getRequestId(), promise);
        try {
            ChannelFuture future = channelPromise.get().writeAndFlush(request);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("request - {} success ", request.getRequestId());
                    } else {
                        log.error("request fail", future.cause());
                    }

                }
            });
        } catch (InterruptedException | ExecutionException e) {
            log.error("get client channel fail", e);
            throw new RuntimeException("get client channel fail");
        }
        return promise;
    }

    private boolean channelActive() {
        Channel channel = channelPromise.getNow();
        return channel != null && channel.isActive();
    }

}
