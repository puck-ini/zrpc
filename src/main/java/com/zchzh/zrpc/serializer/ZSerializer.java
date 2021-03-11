package com.zchzh.zrpc.serializer;

/**
 * @author zengchzh
 * @date 2021/3/10
 * 序列化器
 */
public interface ZSerializer {

    /**
     * 序列化
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Object deserialize(byte[] bytes, Class<T> clazz);
}