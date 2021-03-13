package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.model.request.ZRpcRequest;
import com.zchzh.zrpcstarter.model.respones.ZRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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

    private ZRpcResponse response;

    private ZRpcRequest zRpcRequest;

    private CountDownLatch cdl;

    public NettyClientHandler(ZRpcRequest request){
        this.zRpcRequest = request;
        cdl = new CountDownLatch(1);
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
        this.response = response;
        log.info("client handler : " + response.toString());
        cdl.countDown();
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
        cdl.await();
        return this.response;
    }

    // TODO 添加 userEventTrigger



}
