package org.zchzh.zrpcstarter.model;

import lombok.Builder;
import lombok.Data;
import org.zchzh.zrpcstarter.enums.CompressType;
import org.zchzh.zrpcstarter.enums.MessageType;
import org.zchzh.zrpcstarter.enums.SerializerType;

import java.io.Serializable;


/**
 * @author zengchzh
 * @date 2021/10/11
 */

@Data
@Builder
public class ZRpcMessage implements Serializable {

    private static final long serialVersionUID = -4216851923094056817L;

    private MessageType messageType;

    private SerializerType serializerType;

    private CompressType compressType;

    private Object data;
}
