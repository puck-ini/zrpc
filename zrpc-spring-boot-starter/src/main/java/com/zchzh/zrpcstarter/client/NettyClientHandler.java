package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import com.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
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

    private final ZRpcRequest zRpcRequest;

    private final Promise<ZRpcResponse> responsePromise;

    public NettyClientHandler(ZRpcRequest request){
        this.zRpcRequest = request;
        EventExecutor eventExecutor = GlobalEventExecutor.INSTANCE;
        responsePromise = new DefaultProgressivePromise<>(eventExecutor);
    }

    /**
     * 发起请求
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(" client handler: send request");
        ctx.channel().writeAndFlush(zRpcRequest);
    }

    /**
     * 接受响应
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcResponse response) throws Exception {
        responsePromise.setSuccess(response);
        log.info("client handler : " + response.toString());
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


    public ZRpcResponse getResponse() throws InterruptedException {
        responsePromise.await();
        if (responsePromise.isSuccess()) {
            return responsePromise.getNow();
        }
        return null;
    }

    // TODO 添加 userEventTrigger



}
