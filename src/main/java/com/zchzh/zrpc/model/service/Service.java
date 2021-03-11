package com.zchzh.zrpc.model.service;

import lombok.Data;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Data
public class Service {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务协议
     */
    private String protocol;

    /**
     * 服务地址，格式：IP:Port
     */
    private String address;
}
