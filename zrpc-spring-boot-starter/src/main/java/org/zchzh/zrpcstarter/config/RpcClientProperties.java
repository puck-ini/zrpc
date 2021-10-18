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

    /**
     * 客户端序列化方式
     */
    private SerializerType clientSerializer =  SerializerType.KRYO;

    /**
     * 代理方式
     */
    private String proxy = Constants.JDK;
}
