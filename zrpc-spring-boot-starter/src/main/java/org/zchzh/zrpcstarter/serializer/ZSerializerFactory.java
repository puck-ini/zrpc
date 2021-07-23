package org.zchzh.zrpcstarter.serializer;

import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.zchzh.zrpcstarter.factory.AbstractFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
@Slf4j
@AutoService(AbstractFactory.class)
@JdkSPI(Constants.SERIALIZER)
public class ZSerializerFactory extends AbstractFactory<ZSerializer> {

    @Override
    protected Class<ZSerializer> getType() {
        return ZSerializer.class;
    }

}
