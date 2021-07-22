package org.zchzh.zrpcstarter.constants;

import org.zchzh.zrpcstarter.model.ZRpcRequest;

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

    static {
        BEAT_PING = new ZRpcRequest();
        BEAT_PING.setRequestId(PING + ":" + System.currentTimeMillis());
    }


    public static final String FASTJSON = "fastjson";

    public static final String HESSIAN2 = "hessian";

    public static final String KRYO = "kryo";

    public static final String PROTOSTUFF = "protostuff";




    public static final String RANDOM = "random";

    public static final String WEIGHT_ROUND = "weightround";


    public static final String LOAD_BALANCE = "loadbalance";


    public static final String NACOS = "nacos";

    public static final String ZK = "zookeeper";


    public static final String JDK = "jdk";

    public static final String CGLIB = "cglib";

}
