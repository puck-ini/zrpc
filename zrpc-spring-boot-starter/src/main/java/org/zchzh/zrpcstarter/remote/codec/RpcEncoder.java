package org.zchzh.zrpcstarter.remote.codec;

import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.enums.CompressType;
import org.zchzh.zrpcstarter.enums.SerializerType;
import org.zchzh.zrpcstarter.model.ZRpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;


/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<ZRpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ZRpcMessage msg, ByteBuf out) throws Exception {
        encodeMessageHead(msg, out);
        encodeMessageBody(msg, out);
    }

    /**
     * 编码消息头部
     * @param msg 消息
     * @param out
     */
    private void encodeMessageHead(ZRpcMessage msg, ByteBuf out) {
        out.writeBytes(Constants.MAGIC_NUMBER);
        out.writeByte(msg.getMessageType().getCode());
        SerializerType serializerType = msg.getSerializerType();
        out.writeByte(serializerType.getCode());
        CompressType compressType = msg.getCompressType();
        out.writeByte(compressType.getCode());
    }

    /**
     * 编码消息体
     * @param msg 消息
     * @param out
     * @throws IOException 序列化失败时抛异常
     */
    private void encodeMessageBody(ZRpcMessage msg, ByteBuf out) throws IOException {
        byte[] data = msg.getSerializerType().serialize(msg.getData());
        long start = System.currentTimeMillis();
        byte[] compressData = msg.getCompressType().compress(data);
        log.info("compress time : " + (System.currentTimeMillis() - start));
        log.info(" data len : {}, compress data len : {}", data.length, compressData.length);
        out.writeInt(compressData.length + Constants.HEAD_LEN);
        out.writeBytes(compressData);
    }
}
