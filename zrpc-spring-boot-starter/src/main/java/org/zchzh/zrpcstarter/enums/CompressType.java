package org.zchzh.zrpcstarter.enums;

import org.zchzh.zrpcstarter.compress.Compress;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.FactoryProducer;

/**
 * @author zengchzh
 * @date 2021/10/18
 */
public enum CompressType implements Compress {

    /**
     * 不压缩
     */
    NONE((byte) 0, ""),
    /**
     * gzip 压缩方式
     */
    GZIP((byte) 1, Constants.GZIP)
    ;

    private final byte code;

    private final String name;

    CompressType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CompressType get(byte code) {
        for (CompressType type : CompressType.values()) {
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
    public byte[] compress(byte[] bytes) {
        if (this == NONE) {
            return bytes;
        }
        return ((Compress) FactoryProducer.INSTANCE
                .getInstance(Constants.COMPRESS)
                .getInstance(this.getName()))
                .compress(bytes);
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (this == NONE) {
            return bytes;
        }
        return ((Compress) FactoryProducer.INSTANCE
                .getInstance(Constants.COMPRESS)
                .getInstance(this.getName()))
                .decompress(bytes);
    }
}
