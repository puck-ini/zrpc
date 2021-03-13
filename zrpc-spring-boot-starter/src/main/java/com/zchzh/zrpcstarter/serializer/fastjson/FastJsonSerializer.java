package com.zchzh.zrpcstarter.serializer.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zchzh.zrpcstarter.serializer.ZSerializer;

import java.io.IOException;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
public class FastJsonSerializer implements ZSerializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.config(SerializerFeature.WriteClassName, true);
        serializer.write(object);
        return out.toBytes("UTF-8");
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return JSON.parseObject(new String(bytes), clazz);
    }
}
