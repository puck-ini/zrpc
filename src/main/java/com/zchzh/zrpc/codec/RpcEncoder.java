package com.zchzh.zrpc.codec;

import com.zchzh.zrpc.serializer.ZSerializer;
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

    public RpcEncoder(Class<?> genericClass, ZSerializer zSerializer) {
        this.genericClass = genericClass;
        this.zSerializer = zSerializer;
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
