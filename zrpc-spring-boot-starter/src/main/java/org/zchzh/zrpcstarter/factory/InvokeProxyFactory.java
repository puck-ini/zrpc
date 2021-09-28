package org.zchzh.zrpcstarter.factory;

import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.exception.CommonException;
import org.zchzh.zrpcstarter.factory.AbstractFactory;
import org.zchzh.zrpcstarter.proxy.InvokeProxy;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/7/22
 */
@AutoService(AbstractFactory.class)
@JdkSPI(Constants.PROXY)
public class InvokeProxyFactory extends AbstractFactory<InvokeProxy> {

    @Override
    protected Class<InvokeProxy> getType() {
        return InvokeProxy.class;
    }

}
