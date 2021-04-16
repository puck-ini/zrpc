package com.zchzh.zrpcstarter.server.register;

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
    private Class<?> clazz;

    /**
     * 具体服务
     */
    private Object obj;

    public ServiceObject(String name, Class<?> clazz, Object obj) {
        super();
        this.name = name;
        this.clazz = clazz;
        this.obj = obj;
    }
}
