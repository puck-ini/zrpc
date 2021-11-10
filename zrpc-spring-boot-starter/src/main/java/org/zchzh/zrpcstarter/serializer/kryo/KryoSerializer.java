package org.zchzh.zrpcstarter.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/3/10
 * 使用 kryo 序列化和反序列化
 */

@AutoService(ZSerializer.class)
@JdkSPI(value = Constants.KRYO)
public class KryoSerializer implements ZSerializer {

    private final KryoPool kryoPool = KryoPoolFactory.getKryoPoolInstance();

    @Override
    public <T> byte[] serialize(T object) {
        Kryo kryo = kryoPool.borrow();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        try {
            kryo.writeObject(output, object);
            output.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            kryoPool.release(kryo);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = kryoPool.borrow();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input in = new Input(byteArrayInputStream);
        try {
            T result = kryo.readObject(in, clazz);
            in.close();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            kryoPool.release(kryo);
        }
    }
}
