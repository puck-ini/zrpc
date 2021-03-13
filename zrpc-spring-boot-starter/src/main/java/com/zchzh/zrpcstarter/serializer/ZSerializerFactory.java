package com.zchzh.zrpcstarter.serializer;

import com.zchzh.zrpcstarter.enums.ZSerializerEnums;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
@Slf4j
public class ZSerializerFactory {

    public static ZSerializer getSerializer(String name) {
        try {
            ZSerializerEnums zSerializerEnums = ZSerializerEnums.get(name);
            if (zSerializerEnums != null) {
                return (ZSerializer) Class.forName(zSerializerEnums.getClassName()).newInstance();
            }
        }catch (Exception e) {
            log.error("ZSerializerFactory create Serializer error");
            e.printStackTrace();
        }
        return null;
    }
}
