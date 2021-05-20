package com.zchzh.zrpcstarter.codec;

import com.zchzh.zrpcstarter.serializer.ZSerializer;
import com.zchzh.zrpcstarter.serializer.ZSerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    private ZSerializer zSerializer;

    public RpcEncoder(Class<?> genericClass, String serializerName) {
        this.genericClass = genericClass;
        this.zSerializer = ZSerializerFactory.getInstance(serializerName);
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            byte[] data = zSerializer.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
