package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.cache.GlobalCache;
import com.zchzh.zrpcstarter.cache.ResultCache;
import com.zchzh.zrpcstarter.config.Constants;
import com.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author zengchzh
 * @date 2021/3/10
 *
 * 发送请求接受响应
 */

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<ZRpcResponse> {

    private volatile Channel channel;

    private ZRpcRequest zRpcRequest;

    private Promise<ZRpcResponse> responsePromise;

    public NettyClientHandler() {
        EventExecutor eventExecutor = GlobalEventExecutor.INSTANCE;
        responsePromise = new DefaultProgressivePromise<>(eventExecutor);
    }

    public NettyClientHandler(ZRpcRequest request){
        this.zRpcRequest = request;
        EventExecutor eventExecutor = GlobalEventExecutor.INSTANCE;
        responsePromise = new DefaultProgressivePromise<>(eventExecutor);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    /**
     * 发起请求
     * @param ctx
     * @throws Exception
     */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        if (zRpcRequest != null) {
//            log.info(" client handler : send request /v1");
//            sendRequest(zRpcRequest);
//        }
//        log.error("client handler : send request error");
//    }

    /**
     * 接受响应
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcResponse response) throws Exception {
        log.info("client handler : " + response.toString());
        responsePromise.trySuccess(response);
//        EventExecutor eventExecutor = GlobalEventExecutor.INSTANCE;
//        Promise<ZRpcResponse> promise = new DefaultProgressivePromise<>(eventExecutor);
//        promise.trySuccess(response);
//        ResultCache.INSTANCE.put(response.getRequestId(), promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 实现长连接发送心跳
        if (evt instanceof IdleStateEvent) {
            log.info("client send beat -" + System.currentTimeMillis());
            sendRequest(Constants.BEAT_PING);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        GlobalCache.INSTANCE.remove(Constants.DEFAULT_HANDLE);
    }

    public ZRpcResponse getResponse(String id) throws InterruptedException {
//        Promise<ZRpcResponse> promise = ResultCache.INSTANCE.get(id);
        responsePromise.await();
        if (responsePromise.isSuccess()) {
            return responsePromise.getNow();
        }
        return null;
    }

    public void sendRequest(ZRpcRequest request) {
        ChannelFuture channelFuture = channel.writeAndFlush(request);
//        if (!channelFuture.isSuccess()) {
//            log.error("send request error - " + request.getRequestId());
//        }
    }



}
