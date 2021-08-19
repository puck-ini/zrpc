package org.zchzh.zrpcstarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.zchzh.zrpcstarter.constants.Constants;

/**
 * @author zengchzh
 * @date 2021/7/19
 */
@Data
@EnableConfigurationProperties(RpcClientProperties.class)
@ConfigurationProperties("zrpc")
public class RpcClientProperties {

    /**
     * 服务注册中心
     */
    private String registerAddress = "127.0.0.1:8848";

    /**
     * 注册中心协议
     */
    private String registerProtocol = Constants.NACOS;

//    /**
//     * 服务端暴露端口
//     */
//    private Integer serverPort = 19000;
//    /**
//     * 服务端序列化方式
//     */
//    private String serverSerializer = Constants.KRYO;

    /**
     * 客户端暴露端口
     */
    private Integer clientPort = 19000;
//    /**
//     * 客户端序列化方式
//     */
//    private String clientSerializer = Constants.KRYO;
    /**
     * 代理方式
     */
    private String proxy = Constants.JDK;
}
