package org.zchzh.zrpcstarter.serializer.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.auto.service.AutoService;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/7/13
 *
 * Protostuff 可以理解为  google protobuf 序列化的升级版本
 */
@AutoService(ZSerializer.class)
@JdkSPI(value = Constants.PROTOSTUFF)
public class ProtostuffSerializer implements ZSerializer {

    private Objenesis objenesis = new ObjenesisStd();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        Class clazz = object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = RuntimeSchema.createFrom(clazz);
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        // 可以使用 clazz.newInstance()，但是改方法如果对象没有无参构造函数会报错，而且性能不如objenesis.newInstance(clazz);
        T t = objenesis.newInstance(clazz);
//        T t = clazz.newInstance();
        Schema<T> schema = RuntimeSchema.createFrom(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, t, schema);
        return t;
    }
}
