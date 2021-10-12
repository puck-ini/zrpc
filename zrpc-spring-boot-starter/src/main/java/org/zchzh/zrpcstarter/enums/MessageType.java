package org.zchzh.zrpcstarter.enums;

import org.zchzh.zrpcstarter.model.ZRpcMessage;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/10/11
 */
public enum MessageType {
    /**
     * 正常请求
     */
    REQUEST((byte) 0) {
        @Override
        public void handler(ZRpcMessage message, byte[] data, ZSerializer serializer) throws IOException {
            ZRpcRequest request = serializer.deserialize(data, ZRpcRequest.class);
            message.setData(request);
        }
    },
    /**
     * 正常响应
     */
    RESPONSE((byte) 1) {
        @Override
        public void handler(ZRpcMessage message, byte[] data, ZSerializer serializer) throws IOException {
            ZRpcResponse response = serializer.deserialize(data, ZRpcResponse.class);
            message.setData(response);
        }
    },
    /**
     * 心跳请求
     */
    BEAT_REQ((byte) 2),
    /**
     * 心跳响应
     */
    BEAT_RES((byte) 3),
    /**
     * 请求处理失败
     */
    HANDLER_REQ_ERROR_REQ((byte) 4),
    ;

    private byte code;

    MessageType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static MessageType get(byte code) {
        for (MessageType type : MessageType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + code);
    }

    public void handler(ZRpcMessage message, byte[] data, ZSerializer serializer) throws IOException {
        message.setData(this);
    }
}
