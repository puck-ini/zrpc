package com.zchzh.zrpcstarter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Data
public class ZRpcRequest implements Serializable {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private String version;
}
