package com.zchzh.zrpc.server;

import com.zchzh.zrpc.codec.RpcDecoder;
import com.zchzh.zrpc.codec.RpcEncoder;
import com.zchzh.zrpc.model.request.ZRpcRequest;
import com.zchzh.zrpc.model.respones.ZRpcResponse;
import com.zchzh.zrpc.serializer.ZSerializer;
import com.zchzh.zrpc.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
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

    public NettyServerInitializer(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ZSerializer zSerializer = KryoSerializer.class.newInstance();
        ChannelPipeline channelPipeline = ch.pipeline();
        //
        channelPipeline.addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
        // 传输的最大值
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65536,
                0,
                4,
                0,
                0));
        // server 解码 request
        channelPipeline.addLast(new RpcDecoder(ZRpcRequest.class, zSerializer));
        // server 编码 response
        channelPipeline.addLast(new RpcEncoder(ZRpcResponse.class, zSerializer));
        channelPipeline.addLast(new NettyServerHandler(serviceMap));
    }
}
