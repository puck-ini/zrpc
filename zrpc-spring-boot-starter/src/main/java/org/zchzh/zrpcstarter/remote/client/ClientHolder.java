package org.zchzh.zrpcstarter.remote.client;

import io.netty.channel.Channel;
import org.zchzh.zrpcstarter.exception.CommonException;
import org.zchzh.zrpcstarter.model.ServiceObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengchzh
 * @date 2021/4/27
 */

@Slf4j
public class ClientHolder {


    private static final Map<String, Client> CLIENT_MAP = new ConcurrentHashMap<>();


    public static Client get(ServiceObject so) {
        String key = so.getIp() + so.getPort();
        return CLIENT_MAP.computeIfAbsent(key, i -> {
            Client client = new NettyClient(so);
            client.start();
            return client;
        });
    }

    public static Client get(String ip, int port, String serializer) {
        String key = ip + port;
        return CLIENT_MAP.computeIfAbsent(key, i -> {
            Client client = new NettyClient(ip, port);
            client.start();
            return client;
        });
    }

    public static Client get(Channel channel) {
        for (Map.Entry<String, Client> entry : CLIENT_MAP.entrySet()) {
            Client client = entry.getValue();
            if (!(client instanceof NettyClient)) {
                continue;
            }
            if (Objects.equals(channel, ((NettyClient) client).getChannel())) {
                CLIENT_MAP.get(entry.getKey());
            }
        }
        // 未找到客户端
        throw new CommonException("client not found");
    }


    public static void remove(Channel channel) {
        for (Map.Entry<String, Client> entry : CLIENT_MAP.entrySet()) {
            Client client = entry.getValue();
            if (!(client instanceof NettyClient)) {
                continue;
            }
            if (Objects.equals(channel, ((NettyClient) client).getChannel())) {
                CLIENT_MAP.remove(entry.getKey()).stop();
            }
        }
    }

    public static void put(String key, Client client) {
        CLIENT_MAP.putIfAbsent(key, client);
    }

    public static int size() {
        return CLIENT_MAP.size();
    }
}
