package org.zchzh.zrpcstarter.config;

import org.zchzh.zrpcstarter.protocol.request.ZRpcRequest;

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

}