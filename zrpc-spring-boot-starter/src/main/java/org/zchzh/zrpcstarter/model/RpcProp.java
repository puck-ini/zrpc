package org.zchzh.zrpcstarter.model;

import org.zchzh.zrpcstarter.config.RpcClientProperties;
import org.zchzh.zrpcstarter.config.RpcServerProperties;

/**
 * @author zengchzh
 * @date 2021/10/12
 */
public class RpcProp {

    public static final RpcProp INSTANCE = new RpcProp();

    private RpcServerProperties server;

    private RpcClientProperties client;

    public void put(Object o) {
        if (o instanceof RpcServerProperties) {
            this.server = (RpcServerProperties) o;
        } else if (o instanceof RpcClientProperties) {
            this.client = (RpcClientProperties) o;
        }
    }

    public RpcServerProperties getServer() {
        return server;
    }

    public RpcClientProperties getClient() {
        return client;
    }
}
