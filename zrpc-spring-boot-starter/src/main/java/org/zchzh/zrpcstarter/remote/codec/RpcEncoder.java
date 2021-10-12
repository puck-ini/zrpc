package org.zchzh.zrpcstarter.remote.codec;

import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.enums.SerializerType;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.ZRpcMessage;
import org.zchzh.zrpcstarter.serializer.ZSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
public class RpcEncoder extends MessageToByteEncoder<ZRpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ZRpcMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(Constants.MAGIC_NUMBER);
        out.writeByte(msg.getMessageType().getCode());
        SerializerType serializerType = msg.getSerializerType();
        out.writeByte(serializerType.getCode());
        ZSerializer serializer = (ZSerializer) FactoryProducer.INSTANCE
                .getInstance(Constants.SERIALIZER)
                .getInstance(serializerType.getName());
        byte[] data = serializer.serialize(msg.getData());
        out.writeInt(data.length + Constants.HEAD_LEN);
        out.writeBytes(data);
    }
}
