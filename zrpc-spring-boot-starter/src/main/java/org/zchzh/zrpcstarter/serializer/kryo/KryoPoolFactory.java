package org.zchzh.zrpcstarter.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
public class KryoPoolFactory {

    private static volatile KryoPoolFactory kryoPoolFactory = null;

    private final KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            // 关闭循环应用支持。循环引用：类A中有属性List<B>，而类B中有A a属性
            kryo.setReferences(false);
            // 注册类，多机器部署不建议使用，事先注册类可以提高序列化效率
            kryo.register(ZRpcRequest.class);
            kryo.register(ZRpcResponse.class);

            Kryo.DefaultInstantiatorStrategy strategy
                    = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private final KryoPool kryoPool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory(){}

    /**
     * 单例模式
     * @return
     */
    public static KryoPool getKryoPoolInstance() {
        if (kryoPoolFactory == null) {
            synchronized (KryoPoolFactory.class) {
                if (kryoPoolFactory == null) {
                    kryoPoolFactory = new KryoPoolFactory();
                }
            }
        }
        return kryoPoolFactory.getPool();
    }

    public KryoPool getPool() {
        return kryoPool;
    }
}
