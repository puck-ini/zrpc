package org.zchzh.zrpcstarter.serializer.jdk;

import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.*;

/**
 * @author zengchzh
 * @date 2021/8/5
 */

@AutoService(ZSerializer.class)
@JdkSPI(value = Constants.JDK)
public class JdkSerializer implements ZSerializer {

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(buffer)) {
            output.writeObject(object);
        }
        return buffer.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try (ObjectInputStream input = new ObjectInputStream(in)) {
            try {
                return (T) input.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
