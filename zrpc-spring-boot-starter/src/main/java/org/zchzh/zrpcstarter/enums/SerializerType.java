package org.zchzh.zrpcstarter.enums;

import org.zchzh.zrpcstarter.constants.Constants;

/**
 * @author zengchzh
 * @date 2021/10/11
 */
public enum SerializerType {
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
}
