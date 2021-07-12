package org.zchzh.zrpcstarter.remote.server;

import org.zchzh.zrpcstarter.remote.codec.RpcDecoder;
import org.zchzh.zrpcstarter.remote.codec.RpcEncoder;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.request.ZRpcRequest;
import org.zchzh.zrpcstarter.model.respones.ZRpcResponse;
import org.zchzh.zrpcstarter.serializer.ZSerializer;
import org.zchzh.zrpcstarter.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zengchzh
 * @date 2021/3/10
 * 处理消费者传输到的数据
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 服务列表
     */
    private final Map<String, Object> serviceMap;

    private final String serializerName;

    public NettyServerInitializer(Map<String, Object> serviceMap, String serializerName) {
        this.serviceMap = serviceMap;
        this.serializerName = serializerName;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ZSerializer zSerializer = KryoSerializer.class.newInstance();
        ChannelPipeline channelPipeline = ch.pipeline();
        //
        channelPipeline.addLast(new IdleStateHandler(0, 0, Constants.BEAT_TIME * 3, TimeUnit.SECONDS));
        // 传输的最大值
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65536,
                0,
                4,
                0,
                0));
        // server 解码 request
        channelPipeline.addLast(new RpcDecoder(ZRpcRequest.class, serializerName));
        // server 编码 response
        channelPipeline.addLast(new RpcEncoder(ZRpcResponse.class, serializerName));
        channelPipeline.addLast(new NettyServerHandler(serviceMap));
    }
}
