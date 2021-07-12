package org.zchzh.zrpcstarter.serializer;

import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

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
            JdkSPI serializerName = serializer.getClass().getAnnotation(JdkSPI.class);
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
