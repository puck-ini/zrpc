package org.zchzh.zrpcstarter.remote.client;

import org.zchzh.zrpcstarter.remote.codec.RpcDecoder;
import org.zchzh.zrpcstarter.remote.codec.RpcEncoder;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.remote.handler.ResponseHandler;
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

    private ResponseHandler responseHandler;

    private ZSerializer serializer;


    public NettyClientInitializer() {

    }

    public NettyClientInitializer(ResponseHandler responseHandler, ZSerializer serializer) {
        this.responseHandler = responseHandler;
        this.serializer = serializer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        serializer = KryoSerializer.class.newInstance();
        ChannelPipeline channelPipeline = ch.pipeline();

        // 心跳机制，通过心跳检查对方是否有效,同时限制读和写的空闲时间，超过时间就会触发自定义handler中的userEventTrigger方法
        channelPipeline.addLast(new IdleStateHandler(0, 0, Constants.BEAT_TIME, TimeUnit.SECONDS));
        // client 编码 request
        channelPipeline.addLast(new RpcEncoder(ZRpcRequest.class, serializer));
        // 自定义长度编码器
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        // client 节码 response
        channelPipeline.addLast(new RpcDecoder(ZRpcResponse.class, serializer));
//        channelPipeline.addLast(nettyClientHandler);
        channelPipeline.addLast(new ResponseHandler());
    }
}
