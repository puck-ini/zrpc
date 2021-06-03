package org.zchzh.zrpcstarter.cache;

import org.zchzh.zrpcstarter.client.Client;
import org.zchzh.zrpcstarter.client.TestClient;
import org.zchzh.zrpcstarter.protocol.service.ServiceObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/4/27
 */

@Slf4j
public enum ClientCache {
    /**
     * client map
     */
    MAP;


    private static final Map<String, Client> CLIENT_MAP = new ConcurrentHashMap<>();

    public Client get(String key) {
        Client client = CLIENT_MAP.get(key);
        if (client == null) {
            String[] strings = key.split(":");
            client = new TestClient(strings[1],Integer.parseInt(strings[2]));
            put(key, client);
        }
        return client;
    }

    public void put(String key, Client client) {
        CLIENT_MAP.putIfAbsent(key, client);
    }


    public String makeKey(ServiceObject serviceObject) {
        return serviceObject.getName() + ":" +serviceObject.getAddress();
    }

    public int size() {
        return CLIENT_MAP.size();
    }
}
