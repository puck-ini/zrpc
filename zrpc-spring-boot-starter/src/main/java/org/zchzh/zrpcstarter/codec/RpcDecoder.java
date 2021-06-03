package org.zchzh.zrpcstarter.codec;

import org.zchzh.zrpcstarter.serializer.ZSerializer;
import org.zchzh.zrpcstarter.serializer.ZSerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zengchzh
 * @date 2021/3/10
 */

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    private ZSerializer zSerializer;

    public RpcDecoder(Class<?> genericClass, String serializerName) {
        this.genericClass = genericClass;
        this.zSerializer = ZSerializerFactory.getInstance(serializerName);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Netty 不能保证返回的字节大小，需要加上 in.readableBytes() < 4
        if (in.readableBytes() < 4) {
            return;
        }
        // 用来区分报文头和报文体
        in.markReaderIndex();
        int dataLength = in.readInt();

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(zSerializer.deserialize(data, genericClass));
    }
}
