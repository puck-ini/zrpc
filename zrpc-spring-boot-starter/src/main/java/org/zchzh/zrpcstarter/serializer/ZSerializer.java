package org.zchzh.zrpcstarter.serializer;

import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/3/10
 * 序列化器
 */
public interface ZSerializer {

    /**
     * 序列化
     * @param object 序列化对象
     * @param <T> 序列化对象的类型
     * @return 返回字节数组
     * @throws IOException 序列化失败时
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * @param <T> 反序列化对象的类型
     * @param bytes 反序列化字节数组
     * @param clazz 转换的 java 类
     * @return 返回转换的对象
     * @throws IOException 反序列化失败时
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
