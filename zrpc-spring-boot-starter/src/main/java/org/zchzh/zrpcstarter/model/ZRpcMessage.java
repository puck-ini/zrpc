package org.zchzh.zrpcstarter.model;

import lombok.Data;
import lombok.ToString;
import org.zchzh.zrpcstarter.enums.CompressType;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.enums.SerializerType;

import java.io.Serializable;


/**
 * @author zengchzh
 * @date 2021/10/11
 */

@Data
public class ZRpcMessage implements Serializable {

    private static final long serialVersionUID = -4216851923094056817L;

    private MessageType messageType;

    private SerializerType serializerType;

    private CompressType compressType;

    private Object data;

    ZRpcMessage(MessageType messageType,
                SerializerType serializerType,
                CompressType compressType,
                Object data) {
        this.messageType = messageType;
        this.serializerType = serializerType;
        this.compressType = compressType;
        this.data = data;
    }

    public static ZRpcMessage.ZRpcMessageBuilder builder() {
        return new ZRpcMessage.ZRpcMessageBuilder();
    }

    @ToString
    public static class ZRpcMessageBuilder {
        private MessageType messageType;
        private SerializerType serializerType;
        private CompressType compressType;
        private Object data;

        ZRpcMessageBuilder() {
        }

        public ZRpcMessage.ZRpcMessageBuilder messageType(final MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public ZRpcMessage.ZRpcMessageBuilder serializerType(final SerializerType serializerType) {
            this.serializerType = serializerType;
            return this;
        }

        public ZRpcMessage.ZRpcMessageBuilder compressType(final CompressType compressType) {
            this.compressType = compressType;
            return this;
        }

        public ZRpcMessage.ZRpcMessageBuilder data(final Object data) {
            this.data = data;
            return this;
        }

        public ZRpcMessage.ZRpcMessageBuilder setServerConfig() {
            this.serializerType = RpcProp.INSTANCE.getServer().getServerSerializer();
            this.compressType = RpcProp.INSTANCE.getServer().getServerCompress();
            return this;
        }

        public ZRpcMessage.ZRpcMessageBuilder setClientConfig() {
            this.serializerType = RpcProp.INSTANCE.getClient().getClientSerializer();
            this.compressType = RpcProp.INSTANCE.getClient().getClientCompress();
            return this;
        }

        public ZRpcMessage build() {
            return new ZRpcMessage(this.messageType, this.serializerType, this.compressType, this.data);
        }
    }
}
