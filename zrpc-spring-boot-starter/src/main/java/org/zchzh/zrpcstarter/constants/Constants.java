package org.zchzh.zrpcstarter.constants;

import org.zchzh.zrpcstarter.model.ZRpcRequest;
import org.zchzh.zrpcstarter.model.ZRpcResponse;

/**
 * @author zengchzh
 * @date 2021/3/13
 */
public class Constants {

    public static final String UTF_8 = "UTF-8";

    public static final String DEFAULT_HANDLE = "default-handle";

    public static final int BEAT_TIME = 30;

    public static final ZRpcRequest BEAT_PING;

    public static final String PING = "beat_ping";

    public static final ZRpcResponse REMOVE_CLIENT;

    public static final String REMOVE_ID = "remove-client";

    static {
        BEAT_PING = new ZRpcRequest();
        BEAT_PING.setRequestId(PING + ":" + System.currentTimeMillis());
        REMOVE_CLIENT = new ZRpcResponse();
        REMOVE_CLIENT.setRequestId(REMOVE_ID);
    }


    // serializer spi

    public static final String FASTJSON = "fastjson";

    public static final String HESSIAN2 = "hessian";

    public static final String KRYO = "kryo";

    public static final String PROTOSTUFF = "protostuff";


    // loadbalance spi

    public static final String RANDOM = "random";

    public static final String WEIGHT_ROUND = "weightround";


    // meta data map key
    public static final String LOAD_BALANCE = "loadbalance";


    // register spi

    public static final String NACOS = "nacos";

    public static final String ZK = "zookeeper";


    // factory spi

    public static final String JDK = "jdk";

    public static final String CGLIB = "cglib";

    public static final String CLUSTER = "loadbalance";

    public static final String PROXY = "proxy";

    public static final String REGISTER = "register";

    public static final String SERIALIZER = "serializer";


    // compress spi

    public static final String GZIP = "gzip";

}
