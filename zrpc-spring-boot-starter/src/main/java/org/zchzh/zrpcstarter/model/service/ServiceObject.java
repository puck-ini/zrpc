package org.zchzh.zrpcstarter.model.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author zengchzh
 * @date 2021/3/11
 */


@Data
@Builder
public class ServiceObject implements Serializable {

    /**
     * 服务名称
     */
    private String name;

    private String serviceName;

    private String ip;

    private Integer port;

    /**
     * 服务地址，格式：IP:Port
     */
    private String address;

    private Double weight;

    private Class<?> clazz;

    private Map<String, String> meta;

    /**
     * 类方法名
     */
    private String[] methods;

    /**
     * 完整类路径
     */
    private String path;

    /**
     * 应用程序名 spring.application.name
     */
    private String application;

    /**
     * 服务Class
     */
    private String className;


    private String protocol;

    public ServiceObject(String name) {
        super();
        this.name = name;
    }

    public String getAddress() {
        if (StringUtils.isEmpty(address)) {
            address = ip + ":" + port;
        }
        return address;
    }

    public Class<?> getClazz() {
        if (Objects.isNull(clazz)) {
            try {
                clazz = Class.forName(serviceName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }
}
