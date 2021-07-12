package org.zchzh.zrpcstarter.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zengchzh
 * @date 2021/7/9
 */
public class ServerUtil {

    public static String getHost() {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host;
    }
}
