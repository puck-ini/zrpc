package org.zchzh.zrpcstarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.zchzh.zrpcstarter.constants.Constants;
import org.zchzh.zrpcstarter.enums.SerializerType;

/**
 * @author zengchzh
 * @date 2021/7/19
 */

@Data
@EnableConfigurationProperties(RpcServerProperties.class)
@ConfigurationProperties("zrpc")
public class RpcServerProperties {

    /**
     * 服务注册中心
     */
    private String registerAddress = "127.0.0.1:8848";

    /**
     * 注册中心协议
     */
    private String registerProtocol = Constants.NACOS;

    /**
     * 服务端暴露端口
     */
    private Integer serverPort = 19000;
    /**
     * 服务端序列化方式
     */
    private SerializerType serverSerializer = SerializerType.KRYO;

//    /**
//     * 客户端暴露端口
//     */
//    private Integer clientPort = 19000;
//    /**
//     * 客户端序列化方式
//     */
//    private String clientSerializer = Constants.KRYO;
}
