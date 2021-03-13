package com.zchzh.zrpcstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author zengchzh
 * @date 2021/3/11
 */
@Data
@EnableConfigurationProperties(ZRpcProperty.class)
@ConfigurationProperties("zrpc")
public class ZRpcProperty {

    /**
     * 服务注册中心
     */
    private String registerAddress = "127.0.0.1:2181";

    /**
     * 服务端暴露端口
     */
    private Integer serverPort = 19000;

    /**
     * 服务协议
     */
    private String protocol = "zrpc";

    /**
     * 序列化方式
     */
    private String serializer = "kryo";
}