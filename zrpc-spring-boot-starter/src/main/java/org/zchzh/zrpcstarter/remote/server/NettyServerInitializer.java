package org.zchzh.zrpcstarter.remote.server;

import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.remote.codec.RpcDecoder;
import org.zchzh.zrpcstarter.remote.codec.RpcEncoder;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.remote.handler.RequestHandler;
import org.zchzh.zrpcstarter.serializer.ZSerializer;
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

    private final ZSerializer serializer;

    public NettyServerInitializer(String serializer) {
        this.serializer = (ZSerializer) FactoryProducer.INSTANCE
                .getInstance(Constants.SERIALIZER)
                .getInstance(serializer);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        // 心跳配置
        channelPipeline.addLast(new IdleStateHandler(0, 0, Constants.BEAT_TIME * 3, TimeUnit.SECONDS));
        // 传输的最大值
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(8 * 1024 * 1024,
                6,
                4,
                -10,
                0));
        // server 解码 request
        channelPipeline.addLast(new RpcDecoder());
        // server 编码 response
        channelPipeline.addLast(new RpcEncoder());
        channelPipeline.addLast(new RequestHandler());
    }
}
