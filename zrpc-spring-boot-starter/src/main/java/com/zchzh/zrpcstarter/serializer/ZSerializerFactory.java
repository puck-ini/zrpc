package com.zchzh.zrpcstarter.serializer;

import com.zchzh.zrpcstarter.annotation.SerializerName;
import com.zchzh.zrpcstarter.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
@Slf4j
public class ZSerializerFactory {

    private static final Map<String, ZSerializer> SERIALIZER_MAP = new HashMap<>();

    public static ZSerializer getInstance(String name) {
        return SERIALIZER_MAP.computeIfAbsent(name, k -> get(name));
    }

    private static ZSerializer get(String name) {
        ServiceLoader<ZSerializer> loader =ServiceLoader.load(ZSerializer.class);
        for (ZSerializer serializer : loader) {
            SerializerName serializerName = serializer.getClass().getAnnotation(SerializerName.class);
            if (Objects.isNull(serializerName)) {
                throw new IllegalArgumentException("serializer name can not be empty");
            }
            if (Objects.equals(name, serializerName.value())) {
                return serializer;
            }
        }
        throw new CommonException("invalid serializer config");
    }


}
