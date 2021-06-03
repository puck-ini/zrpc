package org.zchzh.zrpcstarter.client;

import org.zchzh.zrpcstarter.codec.RpcDecoder;
import org.zchzh.zrpcstarter.codec.RpcEncoder;
import org.zchzh.zrpcstarter.config.Constants;
import org.zchzh.zrpcstarter.protocol.request.ZRpcRequest;
import org.zchzh.zrpcstarter.protocol.respones.ZRpcResponse;
import org.zchzh.zrpcstarter.serializer.ZSerializer;
import org.zchzh.zrpcstarter.serializer.kryo.KryoSerializer;
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

    private NettyClientHandler nettyClientHandler;

    private String serializerName;

    public NettyClientInitializer() {
        this.serializerName = Constants.KRYO;
    }

    public NettyClientInitializer(NettyClientHandler nettyClientHandler, String serializerName) {
        this.nettyClientHandler = nettyClientHandler;
        this.serializerName = serializerName;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ZSerializer zSerializer = KryoSerializer.class.newInstance();
        ChannelPipeline channelPipeline = ch.pipeline();

        // 心跳机制，通过心跳检查对方是否有效,同时限制读和写的空闲时间，超过时间就会触发自定义handler中的userEventTrigger方法
        channelPipeline.addLast(new IdleStateHandler(0, 0, Constants.BEAT_TIME, TimeUnit.SECONDS));
        // client 编码 request
        channelPipeline.addLast(new RpcEncoder(ZRpcRequest.class, serializerName));
        // 自定义长度编码器
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        // client 节码 response
        channelPipeline.addLast(new RpcDecoder(ZRpcResponse.class, serializerName));
//        channelPipeline.addLast(nettyClientHandler);
        channelPipeline.addLast(new NettyClientHandler());
    }
}