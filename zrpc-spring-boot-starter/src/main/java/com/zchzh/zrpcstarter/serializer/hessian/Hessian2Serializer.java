package com.zchzh.zrpcstarter.serializer.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
public class Hessian2Serializer implements ZSerializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        System.out.println("Hessian2Serializer serialize ========================");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArrayOutputStream);
        output.writeObject(object);
        output.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        System.out.println("Hessian2Serializer deserialize ========================");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(byteArrayInputStream);
        return (T) input.readObject(clazz);
    }
}
