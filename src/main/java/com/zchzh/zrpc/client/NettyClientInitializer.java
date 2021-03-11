package com.zchzh.zrpc.client;

import com.zchzh.zrpc.codec.RpcDecoder;
import com.zchzh.zrpc.codec.RpcEncoder;
import com.zchzh.zrpc.model.request.ZRpcRequest;
import com.zchzh.zrpc.model.respones.ZRpcResponse;
import com.zchzh.zrpc.serializer.ZSerializer;
import com.zchzh.zrpc.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

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

        // client 编码 request
        channelPipeline.addLast(new RpcEncoder(ZRpcRequest.class, zSerializer));
        // client 节码 response
        channelPipeline.addLast(new RpcDecoder(ZRpcResponse.class, zSerializer));
        channelPipeline.addLast(nettyClientHandler);
    }
}
