package org.zchzh.zrpcstarter.remote.client;

import org.zchzh.zrpcstarter.model.ServiceObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/4/27
 */

@Slf4j
public class ClientHolder {


    private static final Map<String, Client> CLIENT_MAP = new ConcurrentHashMap<>();

    public static Client get(String ip, int port, String serializer) {
        String key = ip + port;
        return CLIENT_MAP.computeIfAbsent(key, i -> {
            Client client = new NettyClient(ip, port, serializer);
            client.start();
            return client;
        });
    }

    public static void put(String key, Client client) {
        CLIENT_MAP.putIfAbsent(key, client);
    }

    public static int size() {
        return CLIENT_MAP.size();
    }
}
