package com.zchzh.zrpcstarter.enums;

import com.zchzh.zrpcstarter.serializer.fastjson.FastJsonSerializer;
import com.zchzh.zrpcstarter.serializer.hessian.Hessian2Serializer;
import com.zchzh.zrpcstarter.serializer.kryo.KryoSerializer;
import lombok.Getter;

/**
 * @author zengchzh
 * @date 2021/3/13
 */

@Getter
public enum  ZSerializerEnums {
    KRYO(KryoSerializer.class.getName(), "kryo"),
    HESSIAN(Hessian2Serializer.class.getName(), "hessian"),
    FASTJSON(FastJsonSerializer.class.getName(), "fastjson");

    private String className;

    private String configName;

    ZSerializerEnums(String className, String configName) {
        this.className = className;
        this.configName = configName;
    }

    public static ZSerializerEnums get(String configName) {
        for (ZSerializerEnums enums: ZSerializerEnums.values()){
            if (enums.getConfigName().equals(configName)){
                return enums;
            }
        }
        return null;
    }
}
