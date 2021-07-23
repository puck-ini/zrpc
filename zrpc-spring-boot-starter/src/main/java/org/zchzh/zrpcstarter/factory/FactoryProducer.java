package org.zchzh.zrpcstarter.factory;


/**
 * @author zengchzh
 * @date 2021/7/23
 */
public class FactoryProducer extends AbstractFactory<AbstractFactory>{

    public static final FactoryProducer INSTANCE = new FactoryProducer();

    @Override
    protected Class<AbstractFactory> getType() {
        return AbstractFactory.class;
    }

}
