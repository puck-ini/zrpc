package org.zchzh.zrpcstarter.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengchzh
 * @date 2021/3/10
 */

@Data
public class ZRpcResponse implements Serializable {
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 调用返回结果
     */
    private Object result;
}
