package org.zchzh.zrpcstarter.serializer.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.SerializerName;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
@AutoService(ZSerializer.class)
@SerializerName(value = Constants.HESSIAN2)
public class Hessian2Serializer implements ZSerializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArrayOutputStream);
        output.writeObject(object);
        output.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(byteArrayInputStream);
        return (T) input.readObject(clazz);
    }
}
