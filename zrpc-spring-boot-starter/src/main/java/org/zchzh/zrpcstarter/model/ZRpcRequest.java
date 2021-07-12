package org.zchzh.zrpcstarter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zengchzh
 * @date 2021/3/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZRpcRequest implements Serializable {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private String version;
}
