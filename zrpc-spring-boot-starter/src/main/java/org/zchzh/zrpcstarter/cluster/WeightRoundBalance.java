package org.zchzh.zrpcstarter.cluster;



import com.google.auto.service.AutoService;
import com.google.common.util.concurrent.AtomicDouble;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.model.ServiceObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zengchzh
 * @date 2021/5/23
 */

@AutoService(LoadBalance.class)
@JdkSPI(Constants.WEIGHT_ROUND)
public class WeightRoundBalance implements LoadBalance{

    private volatile AtomicDouble index;

    private static final Double INCR = 1.0d;

    @Override
    public ServiceObject get(List<ServiceObject> list) {
        double allWeight = list.stream().mapToDouble(ServiceObject::getWeight).sum();
        double number = index.getAndAdd(INCR) % allWeight;
        for (ServiceObject so : list) {
            if (so.getWeight() > number) {
                return so;
            }
            number -= so.getWeight();
        }
        return null;
    }
}
