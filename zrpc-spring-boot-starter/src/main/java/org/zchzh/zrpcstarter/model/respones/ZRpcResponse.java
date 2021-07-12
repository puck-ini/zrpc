package org.zchzh.zrpcstarter.model.respones;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengchzh
 * @date 2021/3/10
 */

@Data
public class ZRpcResponse implements Serializable {

    private String requestId;

    private String error;

    private Object result;
}
