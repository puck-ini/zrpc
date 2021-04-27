package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.cache.ResultCache;
import com.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/4/27
 */
@Slf4j
public class TestClient implements Client {


    private String ip;

    private int port;

    private EventLoopGroup workGroup = new NioEventLoopGroup();

    EventExecutor eventExecutor = GlobalEventExecutor.INSTANCE;

    private final Promise<Channel> promiseChannel = new DefaultPromise<>(eventExecutor);

    private Bootstrap bootstrap;

    public TestClient(String ip, int port) {
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
                    promiseChannel.trySuccess(futureListener.channel());
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

    @SneakyThrows
    @Override
    public void send(ZRpcRequest request) {
//        while (channelActive()) {
//            log.info("connect server : " + new Date());
//            TimeUnit.MILLISECONDS.sleep(1000);
//        }
        promiseChannel.await();
        if (promiseChannel.isSuccess()) {
            log.info("send request" + new Date());
            promiseChannel.getNow().writeAndFlush(request);
            Promise<ZRpcResponse> promise = new DefaultProgressivePromise<>(eventExecutor);
            ResultCache.MAP.put(request.getRequestId(), promise);
            ResultCache.MAP.notifyLock();
        }


    }

    private boolean channelActive() {
        Channel channel = promiseChannel.getNow();
        return channel != null && channel.isActive();
    }


}
