package org.zchzh.zrpcstarter.factory;

import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.compress.Compress;
import org.zchzh.zrpcstarter.constants.Constants;

/**
 * @author zengchzh
 * @date 2021/10/18
 */
@AutoService(AbstractFactory.class)
@JdkSPI(Constants.COMPRESS)
public class CompressFactory extends AbstractFactory<Compress> {

    @Override
    protected Class<Compress> getType() {
        return Compress.class;
    }
}
