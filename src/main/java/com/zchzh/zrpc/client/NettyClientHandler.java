package com.zchzh.zrpc.client;

import com.zchzh.zrpc.model.request.ZRpcRequest;
import com.zchzh.zrpc.model.respones.ZRpcResponse;
import com.zchzh.zrpc.serializer.ZSerializer;
import com.zchzh.zrpc.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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

    public NettyClientHandler(ZRpcRequest request){
        this.zRpcRequest = request;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ZSerializer zSerializer = KryoSerializer.class.newInstance();
        log.info(" client handler: send request");
        ctx.channel().writeAndFlush(zSerializer.serialize(zRpcRequest));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcResponse response) throws Exception {
        this.response = response;
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


    public ZRpcResponse getResponse(){
        return this.response;
    }




}
