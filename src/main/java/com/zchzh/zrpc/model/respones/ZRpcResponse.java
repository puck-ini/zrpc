package com.zchzh.zrpc.model.respones;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

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
