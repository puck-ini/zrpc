package org.zchzh.zrpcstarter.remote.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.exception.CommonException;
import org.zchzh.zrpcstarter.model.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/4/27
 */
@Slf4j
public class NettyClient implements Client {


    private final String ip;

    private final int port;

    private final EventLoopGroup workGroup = new NioEventLoopGroup(1);

    private final Promise<Channel> channelPromise = ImmediateEventExecutor.INSTANCE.newPromise();

    private Bootstrap bootstrap;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public NettyClient(ServiceObject so) {
        this.ip = so.getIp();
        this.port = so.getPort();
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

        ChannelFuture future = bootstrap.connect(ip, port);
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                channelPromise.trySuccess(future1.channel());
            } else {
                log.error("Failed to connect to server, try connect after 10s", future1.cause());
                future1.channel().eventLoop().schedule(this::connect, 10, TimeUnit.SECONDS);
            }
        });
    }

    @Override
    public PendingRequest invoke(ZRpcRequest request) {
        String requestId = request.getRequestId();
        PendingReqHolder.put(requestId, new PendingRequest(request));
        ZRpcMessage message = ZRpcMessage.builder().messageType(MessageType.REQUEST).setClientConfig().data(request).build();
        try {
            Channel channel = channelPromise.get();
            ChannelFuture future = channel.writeAndFlush(message);
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    log.info("request - {} success ", request.getRequestId());
                } else {
                    log.error("request fail", future1.cause());
                    PendingReqHolder.remove(requestId);
                    ClientHolder.remove(channel);
                }

            });
        } catch (InterruptedException | ExecutionException e) {
            log.error("get client channel fail", e);
            throw new CommonException("get client channel fail");
        }
        return PendingReqHolder.get(requestId);
    }

    @Override
    public void stop() {
        workGroup.shutdownGracefully();
    }

    public Channel getChannel() {
        return channelPromise.getNow();
    }

    private boolean channelActive() {
        Channel channel = channelPromise.getNow();
        return channel != null && channel.isActive();
    }

}
