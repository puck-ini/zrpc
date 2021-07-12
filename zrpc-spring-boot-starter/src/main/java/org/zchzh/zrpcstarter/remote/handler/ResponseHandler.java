package org.zchzh.zrpcstarter.remote.handler;

import org.zchzh.zrpcstarter.model.ResponseMap;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author zengchzh
 * @date 2021/3/10
 *
 * 发送请求接受响应
 */

@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<ZRpcResponse> {

    private volatile Channel channel;

    private ZRpcRequest zRpcRequest;

    private Promise<ZRpcResponse> responsePromise;

    public ResponseHandler() {
        EventExecutor eventExecutor = GlobalEventExecutor.INSTANCE;
        responsePromise = new DefaultProgressivePromise<>(eventExecutor);
    }

    public ResponseHandler(ZRpcRequest request){
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
        ResponseMap.pop(response.getRequestId()).trySuccess(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("NettyClientHandler exceptionCaught", cause);
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 实现长连接发送心跳
        if (evt instanceof IdleStateEvent) {
            log.info("client send beat -" + System.currentTimeMillis());
            ctx.channel().writeAndFlush(Constants.BEAT_PING);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    public ZRpcResponse getResponse(String id) throws InterruptedException {
        responsePromise.await();
        if (responsePromise.isSuccess()) {
            return responsePromise.getNow();
        }
        return null;
    }

    public Promise<ZRpcResponse> send(ZRpcRequest request) {
        Promise<ZRpcResponse> promise = ImmediateEventExecutor.INSTANCE.newPromise();
        ResponseMap.put(request.getRequestId(), promise);
        try {
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            log.info("send request" + new Date());
            if (channelFuture.isSuccess()) {
                log.info("send request success");
            }
        } catch (Exception e) {
            log.error("send request error {}", e.getMessage());
        }
        return promise;
    }
}