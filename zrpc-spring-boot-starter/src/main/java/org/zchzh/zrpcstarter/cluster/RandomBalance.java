package org.zchzh.zrpcstarter.cluster;



import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.service.ServiceObject;

import java.util.List;
import java.util.Random;

/**
 * @author zengchzh
 * @date 2021/5/23
 */

@AutoService(LoadBalance.class)
@JdkSPI(Constants.RANDOM)
public class RandomBalance implements LoadBalance {

    private static final Random RANDOM = new Random();

    @Override
    public ServiceObject get(List<ServiceObject> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}
