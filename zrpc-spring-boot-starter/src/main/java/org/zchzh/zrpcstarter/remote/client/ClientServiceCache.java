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
public class ClientServiceCache {


    private static final Map<String, Client> CLIENT_MAP = new ConcurrentHashMap<>();

    public static Client get(String key) {
        Client client = CLIENT_MAP.get(key);
        if (client == null) {
            String[] strings = key.split(":");
            client = new NettyClient(strings[1],Integer.parseInt(strings[2]));
            put(key, client);
        }
        return client;
    }

    public static Client getClient(String ip, int port) {
        String key = ip + port;
        return CLIENT_MAP.computeIfAbsent(key, i -> {
            Client client = new NettyClient(ip, port);
            client.start();
            return client;
        });
    }

    public static void put(String key, Client client) {
        CLIENT_MAP.putIfAbsent(key, client);
    }


    public static String makeKey(ServiceObject serviceObject) {
        return serviceObject.getName() + ":" +serviceObject.getAddress();
    }

    public static int size() {
        return CLIENT_MAP.size();
    }
}
