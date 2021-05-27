package com.zchzh.zrpcstarter.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.auto.service.AutoService;
import com.zchzh.zrpcstarter.annotation.SerializerName;
import com.zchzh.zrpcstarter.config.Constants;
import com.zchzh.zrpcstarter.serializer.ZSerializer;
import com.zchzh.zrpcstarter.serializer.kryo.KryoPoolFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/3/10
 * 使用 kryo 序列化和反序列化
 */

@AutoService(ZSerializer.class)
@SerializerName(value = Constants.KRYO)
public class KryoSerializer implements ZSerializer {

    private final KryoPool kryoPool = KryoPoolFactory.getKryoPoolInstance();

    @Override
    public <T> byte[] serialize(T object) {
        Kryo kryo = kryoPool.borrow();
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)) {
            kryo.writeObject(output, object);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            kryoPool.release(kryo);
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = kryoPool.borrow();
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input in = new Input(byteArrayInputStream);) {
            return kryo.readObject(in, clazz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            kryoPool.release(kryo);
        }
    }
}
