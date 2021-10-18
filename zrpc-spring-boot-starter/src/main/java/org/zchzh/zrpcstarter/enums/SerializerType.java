package org.zchzh.zrpcstarter.enums;

import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;
import org.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/10/11
 */
public enum SerializerType implements ZSerializer {
    /**
     * FASTJSON 序列化
     */
    FASTJSON((byte) 0, Constants.FASTJSON),
    /**
     * HESSIAN2 序列化
     */
    HESSIAN2((byte) 1, Constants.HESSIAN2),
    /**
     * JDK 序列化
     */
    JDK((byte) 2, Constants.JDK),
    /**
     * KRYO 序列化
     */
    KRYO((byte) 3, Constants.KRYO),
    /**
     * PROTOSTUFF 序列化
     */
    PROTOSTUFF((byte) 4, Constants.PROTOSTUFF);


    private final byte code;

    private final String name;

    SerializerType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SerializerType get(byte code) {
        for (SerializerType type : SerializerType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + code);
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return ((ZSerializer) FactoryProducer.INSTANCE
                .getInstance(Constants.SERIALIZER)
                .getInstance(this.getName())).serialize(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return ((ZSerializer) FactoryProducer.INSTANCE
                .getInstance(Constants.SERIALIZER)
                .getInstance(this.getName())).deserialize(bytes, clazz);
    }
}
