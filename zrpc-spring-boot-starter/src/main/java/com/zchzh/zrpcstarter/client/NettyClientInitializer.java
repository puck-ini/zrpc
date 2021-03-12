package com.zchzh.zrpcstarter.client;

import com.zchzh.zrpcstarter.codec.RpcDecoder;
import com.zchzh.zrpcstarter.codec.RpcEncoder;
import com.zchzh.zrpcstarter.model.request.ZRpcRequest;
import com.zchzh.zrpcstarter.model.respones.ZRpcResponse;
import com.zchzh.zrpcstarter.serializer.ZSerializer;
import com.zchzh.zrpcstarter.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyClientHandler nettyClientHandler;

    public NettyClientInitializer(NettyClientHandler nettyClientHandler) {
        this.nettyClientHandler = nettyClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ZSerializer zSerializer = KryoSerializer.class.newInstance();
        ChannelPipeline channelPipeline = ch.pipeline();

        channelPipeline.addLast(new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
        // client 编码 request
        channelPipeline.addLast(new RpcEncoder(ZRpcRequest.class, zSerializer));
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        // client 节码 response
        channelPipeline.addLast(new RpcDecoder(ZRpcResponse.class, zSerializer));
        channelPipeline.addLast(nettyClientHandler);
    }
}
