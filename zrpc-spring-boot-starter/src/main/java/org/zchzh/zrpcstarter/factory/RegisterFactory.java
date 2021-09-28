package org.zchzh.zrpcstarter.factory;

import com.google.auto.service.AutoService;
import org.zchzh.zrpcstarter.annotation.JdkSPI;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.exception.CommonException;
import org.zchzh.zrpcstarter.factory.AbstractFactory;
import org.zchzh.zrpcstarter.register.Register;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * @author zengchzh
 * @date 2021/7/21
 */

@AutoService(AbstractFactory.class)
@JdkSPI(Constants.REGISTER)
public class RegisterFactory extends AbstractFactory<Register> {

    @Override
    protected Class<Register> getType() {
        return Register.class;
    }

}
