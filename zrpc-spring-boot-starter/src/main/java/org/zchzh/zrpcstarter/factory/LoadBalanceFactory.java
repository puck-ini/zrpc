package org.zchzh.zrpcstarter.factory;



import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.cluster.LoadBalance;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.factory.AbstractFactory;

/**
 * @author zengchzh
 * @date 2021/7/11
 */

@AutoService(AbstractFactory.class)
@JdkSPI(Constants.CLUSTER)
public class LoadBalanceFactory extends AbstractFactory<LoadBalance> {

    @Override
    protected Class<LoadBalance> getType() {
        return LoadBalance.class;
    }

}
