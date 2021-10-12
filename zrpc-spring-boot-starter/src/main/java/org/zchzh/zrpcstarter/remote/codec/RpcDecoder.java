package org.zchzh.zrpcstarter.remote.codec;

import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.enums.SerializerType;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.model.ZRpcMessage;
import org.zchzh.zrpcstarter.serializer.ZSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

/**
 * @author zengchzh
 * @date 2021/3/10
 */

public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       if (in.readableBytes() >= Constants.HEAD_LEN) {
           checkMagicNum(in);
           byte messageCode = in.readByte();
           MessageType messageType = MessageType.get(messageCode);
           byte serializerCode = in.readByte();
           SerializerType serializerType = SerializerType.get(serializerCode);
           ZRpcMessage message = ZRpcMessage.builder().messageType(messageType).serializerType(serializerType).build();
           int dataLen = in.readInt();
           byte[] data = new byte[dataLen - Constants.HEAD_LEN];
           in.readBytes(data);
           ZSerializer serializer = (ZSerializer) FactoryProducer.INSTANCE
                   .getInstance(Constants.SERIALIZER)
                   .getInstance(serializerType.getName());
           messageType.handler(message, data, serializer);
           out.add(message);
       }
    }


    public void checkMagicNum(ByteBuf in) {
        int len = Constants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != Constants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic : " + Arrays.toString(tmp));
            }
        }
    }


}
