package org.zchzh.zrpcstarter.model.service;

import lombok.Data;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Data
public class ServiceObject {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务地址，格式：IP:Port
     */
    private String address;

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
}
