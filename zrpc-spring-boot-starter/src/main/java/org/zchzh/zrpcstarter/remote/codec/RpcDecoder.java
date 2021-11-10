package org.zchzh.zrpcstarter.remote.codec;

import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.enums.CompressType;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.enums.SerializerType;
import org.zchzh.zrpcstarter.model.ZRpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author zengchzh
 * @date 2021/3/10
 */

@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       if (in.readableBytes() >= Constants.HEAD_LEN) {
           ZRpcMessage message = decodeMessageHead(in);
           decodeMessageBody(message, in);
           out.add(message);
       }
    }

    /**
     * 解码消息头部
     * @param in 待解码数据
     * @return 返回 ZRpcMessage 对象
     */
    private ZRpcMessage decodeMessageHead(ByteBuf in) {
        checkMagicNum(in);
        byte messageCode = in.readByte();
        MessageType messageType = MessageType.get(messageCode);
        byte serializerCode = in.readByte();
        SerializerType serializerType = SerializerType.get(serializerCode);
        byte compressCode = in.readByte();
        CompressType compressType = CompressType.get(compressCode);
        return ZRpcMessage.builder()
                .messageType(messageType)
                .serializerType(serializerType)
                .compressType(compressType)
                .build();
    }

    /**
     * 解码消息体
     * @param message 消息
     * @param in 待解码数据
     * @throws IOException 反序列化失败时抛异常
     */
    private void decodeMessageBody(ZRpcMessage message, ByteBuf in) throws IOException {
        int dataLen = in.readInt();
        byte[] data = new byte[dataLen - Constants.HEAD_LEN];
        in.readBytes(data);
        long start = System.currentTimeMillis();
        byte[] decompressData = message.getCompressType().decompress(data);
        log.info("decompress time : " + (System.currentTimeMillis() - start));
        switch (message.getMessageType()) {
            case REQUEST:
                message.setData(message.getSerializerType().deserialize(decompressData, ZRpcRequest.class));
                break;
            case RESPONSE:
                message.setData(message.getSerializerType().deserialize(decompressData, ZRpcResponse.class));
                break;
            case BEAT_REQ:
            case BEAT_RES:
            case HANDLER_REQ_ERROR_REQ:
            default:
                break;
        }
    }


    /**
     * 检查魔数
     * @param in
     */
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
