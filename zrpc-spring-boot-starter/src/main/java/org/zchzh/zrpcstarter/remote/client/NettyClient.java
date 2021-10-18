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

import java.util.concurrent.CompletableFuture;
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
    public CompletableFuture<ZRpcResponse> invoke(ZRpcRequest request) {
        CompletableFuture<ZRpcResponse> resFuture = new CompletableFuture<>();
        String requestId = request.getRequestId();
        ResponseHolder.put(requestId, resFuture);
        ZRpcMessage message = ZRpcMessage.builder()
                .messageType(MessageType.REQUEST)
                .serializerType(RpcProp.INSTANCE.getClient().getClientSerializer())
                .compressType(RpcProp.INSTANCE.getClient().getClientCompress())
                .data(request)
                .build();
        try {
            Channel channel =  channelPromise.get();
            ChannelFuture future =channel.writeAndFlush(message);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("request - {} success ", request.getRequestId());
                    } else {
                        log.error("request fail", future.cause());
                        ResponseHolder.remove(requestId);
                        ClientHolder.remove(channel);
                    }

                }
            });
        } catch (InterruptedException | ExecutionException e) {
            log.error("get client channel fail", e);
            throw new CommonException("get client channel fail");
        }
        return resFuture;
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
