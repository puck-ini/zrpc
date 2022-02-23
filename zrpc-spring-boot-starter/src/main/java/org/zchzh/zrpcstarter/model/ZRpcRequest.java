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

    private static final long serialVersionUID = -2533590886135497522L;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 请求的接口名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 调用参数
     */
    private Object[] parameters;
}
