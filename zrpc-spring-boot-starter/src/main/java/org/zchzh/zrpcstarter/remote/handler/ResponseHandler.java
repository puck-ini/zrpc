package org.zchzh.zrpcstarter.remote.handler;

import io.netty.channel.*;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.model.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.remote.client.ClientHolder;

import java.util.Date;

/**
 * @author zengchzh
 * @date 2021/3/10
 *
 * 发送请求接受响应
 */

@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<ZRpcMessage> {

    /**
     * 接受响应
     * @param ctx
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZRpcMessage message) throws Exception {
        if (message.getMessageType() == MessageType.BEAT_RES) {
            return;
        }
        // 处理请求太大报错问题
        ZRpcResponse response = (ZRpcResponse) message.getData();
        String id = response.getRequestId();
        if (message.getMessageType() == MessageType.HANDLER_REQ_ERROR_REQ) {
            PendingReqHolder.remove(id);
            ClientHolder.remove(ctx.channel());
        } else {
            PendingReqHolder.pop(id).complete(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("ResponseHandler exceptionCaught", cause);
        ClientHolder.remove(ctx.channel());
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 实现长连接发送心跳
        if (evt instanceof IdleStateEvent) {
            log.info("client send beat -" + new Date());
            ZRpcMessage message = ZRpcMessage.builder().messageType(MessageType.BEAT_REQ).setClientConfig().build();
            ctx.channel().writeAndFlush(message);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
